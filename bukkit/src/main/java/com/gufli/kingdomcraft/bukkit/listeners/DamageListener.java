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

import com.gufli.kingdomcraft.api.entity.PlatformPlayer;
import com.gufli.kingdomcraft.api.event.EventResult;
import com.gufli.kingdomcraft.bukkit.KingdomCraftBukkitPlugin;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityEvent;

public class DamageListener implements Listener {

    private final KingdomCraftBukkitPlugin plugin;

    public DamageListener(KingdomCraftBukkitPlugin plugin) {
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
        if ( !plugin.getKdc().getConfig().isWorldEnabled(entity.getWorld().getName()) ) {
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

        EventResult result = plugin.getKdc().getEventDispatcher().dispatchPlayerAttack(p, d);
        if ( result == EventResult.DENY ) {
            if ( damager instanceof Projectile ) {
                damager.remove();
            }

            e.setCancelled(true);
        }
    }
}
