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

import com.guflan.kingdomcraft.api.entity.PlatformPlayer;
import com.guflan.kingdomcraft.bukkit.KingdomCraftBukkitPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathListener implements Listener {

    private final KingdomCraftBukkitPlugin plugin;

    public DeathListener(KingdomCraftBukkitPlugin plugin) {
        this.plugin = plugin;
    }

    // respawn

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if ( !plugin.getKdc().getConfig().isWorldEnabled(event.getEntity().getWorld().getName()) ) {
            return;
        }

        if ( event.getEntity().getKiller() != null ) {
            if ( plugin.getKdc().getConfig().getOnKillMessage() == null
                    || plugin.getKdc().getConfig().getOnKillMessage().equals("") ) {
                return;
            }

            PlatformPlayer p = plugin.getKdc().getPlayer(event.getEntity().getUniqueId());
            PlatformPlayer k = plugin.getKdc().getPlayer(event.getEntity().getKiller().getUniqueId());

            String msg = plugin.getKdc().getConfig().getOnKillMessage();
            msg = plugin.getKdc().getPlaceholderManager().handle(p, msg);
            msg = plugin.getKdc().getPlaceholderManager().handle(k, msg, "killer_");
            msg = plugin.getKdc().getMessageManager().colorify(msg);
            event.setDeathMessage(msg);
            return;
        }

        if ( plugin.getKdc().getConfig().getOnDeathMessage() == null
                || plugin.getKdc().getConfig().getOnDeathMessage().equals("") ) {
            return;
        }

        PlatformPlayer p = plugin.getKdc().getPlayer(event.getEntity().getUniqueId());

        String msg = plugin.getKdc().getConfig().getOnDeathMessage();
        msg = plugin.getKdc().getPlaceholderManager().handle(p, msg);
        msg = plugin.getKdc().getMessageManager().colorify(msg);
        event.setDeathMessage(msg);
    }
}
