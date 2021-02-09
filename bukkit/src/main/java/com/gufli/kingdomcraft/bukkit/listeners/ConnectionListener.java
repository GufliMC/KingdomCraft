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
import com.gufli.kingdomcraft.api.events.PlayerLoadedEvent;
import com.gufli.kingdomcraft.bukkit.KingdomCraftBukkitPlugin;
import com.gufli.kingdomcraft.bukkit.entity.BukkitPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ConnectionListener implements Listener {

    private final KingdomCraftBukkitPlugin plugin;

    public ConnectionListener(KingdomCraftBukkitPlugin plugin) {
        this.plugin = plugin;
        plugin.getKdc().getEventManager().addListener(com.gufli.kingdomcraft.api.events.PlayerLoginEvent.class, this::onLogin);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onLogin(PlayerLoginEvent e) {
        if ( e.getResult() != PlayerLoginEvent.Result.ALLOWED ) {
            return;
        }
        plugin.getScheduler().executeAsync(() -> {
            if ( !plugin.getKdc().onLogin(new BukkitPlayer(e.getPlayer())) ) {
                e.getPlayer().kickPlayer(plugin.getKdc().getMessages().getPrefix() +
                        ChatColor.RED + "An error occured! Could not load your user data.");
            }
        });
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent e) {
        PlatformPlayer player = plugin.getKdc().getPlayer(e.getPlayer().getUniqueId());
        plugin.getKdc().onQuit(player);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent e) {
        PlatformPlayer player = plugin.getKdc().getPlayer(e.getPlayer().getUniqueId());
        if ( player == null ) {
            return; // player is still loading
        }
        if ( player.has("LOADED") ) {
            return;
        }
        player.set("LOADED", true);
        plugin.getKdc().getEventManager().dispatch(new PlayerLoadedEvent(player));
    }

    public void onLogin(com.gufli.kingdomcraft.api.events.PlayerLoginEvent e) {
        PlatformPlayer player = e.getPlayer();
        plugin.getScheduler().sync().execute(() -> {
            if ( Bukkit.getPlayer(player.getUniqueId()) == null ) {
                return; // player is still joining
            }
            if ( player.has("LOADED") ) {
                return;
            }
            player.set("LOADED", true);
            plugin.getKdc().getEventManager().dispatch(new PlayerLoadedEvent(player));
        });
    }
}
