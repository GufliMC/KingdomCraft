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
import com.gufli.kingdomcraft.api.entity.PlatformPlayer;
import com.gufli.kingdomcraft.api.events.PlayerLeaveEvent;
import com.gufli.kingdomcraft.api.events.PlayerLoadedEvent;
import com.gufli.kingdomcraft.bukkit.KingdomCraftBukkitPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinQuitListener implements Listener {

    private final KingdomCraftBukkitPlugin plugin;

    public JoinQuitListener(KingdomCraftBukkitPlugin plugin) {
        this.plugin = plugin;

        plugin.getKdc().getEventManager().addListener(PlayerLoadedEvent.class, this::onJoin);
        plugin.getKdc().getEventManager().addListener(PlayerLeaveEvent.class, this::onLeave);
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

    public void onJoin(PlayerLoadedEvent e) {
        String msg = plugin.getKdc().getConfig().getOnJoinMessage();
        if ( msg == null || msg.equals("") ) {
            return;
        }

        send(e.getPlayer(), msg);
    }

    public void onLeave(PlayerLeaveEvent e) {
        String msg = plugin.getKdc().getConfig().getOnLeaveMessage();
        if ( msg == null || msg.equals("")) {
            return;
        }

        send(e.getPlayer(), msg);
    }

    private void send(PlatformPlayer player, String msg) {
        if ( msg == null || msg.equals("")) {
            return;
        }

        msg = plugin.getKdc().getPlaceholderManager().handle(player, msg);
        msg = plugin.getKdc().getMessages().colorify(msg);

        if ( plugin.getKdc().getConfig().showJoinAndLeaveKingdomOnly() ) {
            User user = player.getUser();
            if ( user.getKingdom() == null ) {
                return;
            }

            String finalMsg = msg;
            plugin.getKdc().getOnlinePlayers().stream()
                    .filter(p -> p.getUser().getKingdom() == user.getKingdom())
                    .forEach(p -> p.sendMessage(finalMsg));
        } else {
            String finalMsg = msg;
            plugin.getKdc().getOnlinePlayers().forEach(p -> p.sendMessage(finalMsg));
        }
    }
}
