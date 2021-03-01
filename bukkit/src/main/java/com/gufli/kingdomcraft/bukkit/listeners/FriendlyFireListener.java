/*
 * This file is part of KingdomCraft.
 *
 * KingdomCraft is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * KingdomCraft is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with KingdomCraft. If not, see <https://www.gnu.org/licenses/>.
 */

package com.gufli.kingdomcraft.bukkit.listeners;

import com.gufli.kingdomcraft.api.domain.Kingdom;
import com.gufli.kingdomcraft.api.domain.Relation;
import com.gufli.kingdomcraft.api.domain.RelationType;
import com.gufli.kingdomcraft.api.domain.User;
import com.gufli.kingdomcraft.api.entity.PlatformPlayer;
import com.gufli.kingdomcraft.api.events.FriendlyFireCancelEvent;
import com.gufli.kingdomcraft.bukkit.KingdomCraftBukkitPlugin;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerFishEvent;

public class FriendlyFireListener implements Listener {

    private final KingdomCraftBukkitPlugin plugin;

    public FriendlyFireListener(KingdomCraftBukkitPlugin plugin) {
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

    @EventHandler
    public void onPlayerFish(PlayerFishEvent e) {
        if ( e.getState() == PlayerFishEvent.State.CAUGHT_ENTITY ) {
            handleEvent(e, e.getCaught(), e.getPlayer());
        }
    }

    private <T extends Cancellable> void handleEvent(T e, Entity entity, Entity damager) {
        if ( !plugin.getKdc().getConfig().isWorldEnabled(entity.getWorld().getName()) ) {
            return;
        }

        // friendly fire is enabled, do nothing
        if ( plugin.getKdc().getConfig().isFriendlyFireEnabled() ) {
            return;
        }

        PlatformPlayer p;
        if ( entity instanceof Player) {
            p = plugin.getKdc().getPlayer(entity.getUniqueId());
        } else {
            return;
        }

        PlatformPlayer d;
        if ( damager instanceof Player) {
            d = plugin.getKdc().getPlayer(damager.getUniqueId());
        } else if ( damager instanceof Projectile && ((Projectile) damager).getShooter() instanceof Player) {
            d = plugin.getKdc().getPlayer(((Player) ((Projectile) damager).getShooter()).getUniqueId());
        }
        else {
            return;
        }

        // Ignore when the player is not a real player (like an npc from another plugin)
        if ( p == null || d == null ) {
            return;
        }

        // no friendly fire check when attacking yourself
        if ( p == d ) {
            return;
        }

        // admin bypasses
        if ( d.isAdmin() && d.hasPermission("kingdom.admin.friendlyfire") ) {
            return;
        }

        User u1 = plugin.getKdc().getUser(p);
        User u2 = plugin.getKdc().getUser(d);

        if ( u1 == null || u2 == null ) {
            return;
        }

        Kingdom k1 = u1.getKingdom();
        Kingdom k2 = u2.getKingdom();

        if ( k1 == null || k2 == null ) {
            return;
        }

        if ( k1 != k2 ) {
            Relation rel = plugin.getKdc().getRelation(k1, k2);
            RelationType type = rel == null ? RelationType.NEUTRAL : rel.getType();
            if ( !plugin.getKdc().getConfig().getFriendlyFireRelationTypes().contains(type) ) {
                return;
            }
        }

        // FRIENDLY FIRE
        FriendlyFireCancelEvent event = new FriendlyFireCancelEvent(p, d);
        plugin.getKdc().getEventManager().dispatch(event);

        // allowed = ignore, do nothing
        if ( event.isAllowed() ) {
            return;
        }

        e.setCancelled(true);

        // remove sticking arrows
        if ( damager instanceof Arrow ) {
            damager.remove();
        }

        long lastNotification = 0;
        if ( d.has("LAST_FRIENDLYFIRE_NOTIFICATION") ) {
            lastNotification = d.get("LAST_FRIENDLYFIRE_NOTIFICATION", Long.class);
        }

        if ( System.currentTimeMillis() - lastNotification > 5000 ) {
            plugin.getKdc().getMessages().send(d, "friendlyFire");
            d.set("LAST_FRIENDLYFIRE_NOTIFICATION", System.currentTimeMillis());
        }
    }
}
