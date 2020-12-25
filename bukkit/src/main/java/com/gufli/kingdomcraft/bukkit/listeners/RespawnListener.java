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

import com.gufli.kingdomcraft.api.domain.User;
import com.gufli.kingdomcraft.bukkit.KingdomCraftBukkitPlugin;
import com.gufli.kingdomcraft.bukkit.util.LocationConverter;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class RespawnListener implements Listener {

    private final KingdomCraftBukkitPlugin plugin;

    public RespawnListener(KingdomCraftBukkitPlugin plugin) {
        this.plugin = plugin;
    }

    // respawn

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onRespawn(PlayerRespawnEvent event) {
        if ( !plugin.getKdc().getConfig().isWorldEnabled(event.getRespawnLocation().getWorld().getName()) ) {
            return;
        }

        if ( !plugin.getKdc().getConfig().respawnAtKingdom() ) {
            return;
        }

        User user = plugin.getKdc().getOnlineUser(event.getPlayer().getUniqueId());
        if ( user.getKingdom() == null || user.getKingdom().getSpawn() == null ) {
            return;
        }

        Location loc = LocationConverter.convert(user.getKingdom().getSpawn());
        if ( loc == null ) {
            return;
        }

        event.setRespawnLocation(loc);
    }
}
