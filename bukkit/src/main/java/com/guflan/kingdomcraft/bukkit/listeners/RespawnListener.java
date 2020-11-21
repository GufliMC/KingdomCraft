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

package com.guflan.kingdomcraft.bukkit.listeners;

import com.guflan.kingdomcraft.api.domain.User;
import com.guflan.kingdomcraft.api.entity.PlatformPlayer;
import com.guflan.kingdomcraft.bukkit.KingdomCraftBukkitPlugin;
import com.guflan.kingdomcraft.bukkit.util.LocationConverter;
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

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onRespawn(PlayerRespawnEvent event) {
        if ( !plugin.getKdc().getConfig().isWorldEnabled(event.getRespawnLocation().getWorld().getName()) ) {
            return;
        }

        if ( plugin.getKdc().getConfig().respawnAtKingdom() ) {
            User user = plugin.getKdc().getOnlineUser(event.getPlayer().getUniqueId());
            if ( user.getKingdom() != null && user.getKingdom().getSpawn() != null ) {
                event.setRespawnLocation(LocationConverter.convert(user.getKingdom().getSpawn()));
            }
        }
    }
}
