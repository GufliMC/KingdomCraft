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
import com.gufli.kingdomcraft.api.event.EventListener;
import com.gufli.kingdomcraft.bukkit.KingdomCraftBukkitPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinQuitListener implements Listener, EventListener {

    private final KingdomCraftBukkitPlugin plugin;

    public JoinQuitListener(KingdomCraftBukkitPlugin plugin) {
        this.plugin = plugin;
        plugin.getKdc().getEventManager().addListener(this);
    }

    // join & quit messages

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if ( plugin.getKdc().getConfig().getOnJoinMessage() != null
                && !plugin.getKdc().getConfig().getOnJoinMessage().equals("") ) {
            e.setJoinMessage(null);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        if ( plugin.getKdc().getConfig().getOnLeaveMessage() != null
                && !plugin.getKdc().getConfig().getOnLeaveMessage().equals("") ) {
            e.setQuitMessage(null);
        }
    }

    // kingdomcraft events

    @Override
    public void onJoin(PlatformPlayer player) {
        String msg = plugin.getKdc().getConfig().getOnJoinMessage();
        if ( msg != null ) {
            msg = plugin.getKdc().getPlaceholderManager().handle(player, msg);
            msg = plugin.getKdc().getMessageManager().colorify(msg);

            for (PlatformPlayer p : plugin.getKdc().getOnlinePlayers()) {
                p.sendMessage(msg);
            }
        }
    }

    @Override
    public void onQuit(PlatformPlayer player) {
        String msg = plugin.getKdc().getConfig().getOnLeaveMessage();
        if ( msg != null ) {
            msg = plugin.getKdc().getPlaceholderManager().handle(player, msg);
            msg = plugin.getKdc().getMessageManager().colorify(msg);

            for (PlatformPlayer p : plugin.getKdc().getOnlinePlayers()) {
                p.sendMessage(msg);
            }
        }
    }
}
