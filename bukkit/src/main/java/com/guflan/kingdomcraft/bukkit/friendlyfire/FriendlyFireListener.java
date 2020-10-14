/*
 * This file is part of KingdomCraft.
 *
 * KingdomCraft is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * KingdomCraft is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with KingdomCraft. If not, see <https://www.gnu.org/licenses/>.
 */

package com.guflan.kingdomcraft.bukkit.friendlyfire;

import com.guflan.kingdomcraft.api.KingdomCraft;
import com.guflan.kingdomcraft.api.domain.models.Kingdom;
import com.guflan.kingdomcraft.api.domain.models.Relation;
import com.guflan.kingdomcraft.api.domain.models.RelationType;
import com.guflan.kingdomcraft.api.domain.models.User;
import com.guflan.kingdomcraft.api.entity.Player;
import com.guflan.kingdomcraft.api.events.PlayerAttackPlayerEvent;
import com.guflan.kingdomcraft.bukkit.BukkitKingdomCraftPlugin;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityEvent;

public class FriendlyFireListener implements Listener {

    private final BukkitKingdomCraftPlugin plugin;

    public FriendlyFireListener(BukkitKingdomCraftPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerDamage(EntityDamageByEntityEvent e) {
        handleEvent(e, e.getEntity(), e.getDamager());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEntityCombustByEntity(EntityCombustByEntityEvent e) {
        handleEvent(e, e.getEntity(), e.getCombuster());
    }

    private <T extends EntityEvent & Cancellable> void handleEvent(T e, Entity entity, Entity damager) {
        if ( !plugin.getConfiguration().isWorldEnabled(entity.getWorld().getName()) ) {
            return;
        }


        Player p;
        if ( entity instanceof Player) {
            p = KingdomCraft.getPlayer(entity.getUniqueId());
        } else {
            return;
        }

        Player d;
        if ( damager instanceof Player ) {
            d = KingdomCraft.getPlayer(damager.getUniqueId());
        } else if ( damager instanceof Projectile && ((Projectile) damager).getShooter() instanceof Player) {
            d = KingdomCraft.getPlayer(((Player) ((Projectile) damager).getShooter()).getUniqueId());
        }
        else {
            return;
        }

        // Attacking an npc from another plugin will trigger this event
        if ( p == null || d == null ) {
            return;
        }

        PlayerAttackPlayerEvent event = new PlayerAttackPlayerEvent(p, d);
        KingdomCraft.getEventManager().playerAttack(event);

        if ( event.getResult() == PlayerAttackPlayerEvent.Result.DENY ) {
            e.setCancelled(true);
            return;
        }

        if ( event.getResult() == PlayerAttackPlayerEvent.Result.ALLOW ) {
            return;
        }

        User u1 = KingdomCraft.getUser(p);
        User u2 = KingdomCraft.getUser(d);

        if ( d.hasPermission("kingdom.friendlyfire.bypass") ) {
            return;
        }

        Kingdom k1 = u1.getKingdom();
        Kingdom k2 = u2.getKingdom();

        if ( k1 == null || k2 == null ) {
            return;
        }

        if ( k1 != k2 ) {
            Relation rel = KingdomCraft.getRelation(k1, k2);
            RelationType type = rel == null ? RelationType.NEUTRAL : rel.getType();
            if ( !plugin.getConfiguration().getFriendlyFireRelationTypes().contains(type) ) {
                return;
            }
        }

        e.setCancelled(true);
        if ( damager instanceof Projectile ) {
            damager.remove();
        }

        long lastNotification = 0;
        if ( d.has("LAST_FRIENDLYFIRE_NOTIFICATION") ) {
            lastNotification = d.get("LAST_FRIENDLYFIRE_NOTIFICATION", Long.class);
        }

        if ( System.currentTimeMillis() - lastNotification > 5000 ) {
            KingdomCraft.getMessageManager().send(d, "friendlyFire");
            d.set("LAST_FRIENDLYFIRE_NOTIFICATION", System.currentTimeMillis());
        }
    }
}
