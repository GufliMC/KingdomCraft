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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.logging.Level;

public class ConnectionListener implements Listener {

    private final KingdomCraftBukkitPlugin plugin;
    private final Map<UUID, Consumer<PlatformPlayer>> unregisterdPlayers = new ConcurrentHashMap<>();

    public ConnectionListener(KingdomCraftBukkitPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLogin(AsyncPlayerPreLoginEvent e) {
        Consumer<PlatformPlayer> register = plugin.getKdc().onLogin(e.getUniqueId(), e.getName());
        if ( register == null ) {
            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                    ChatColor.GOLD + "[KingdomCraft]\n\n" + ChatColor.RED
                    + "The server was unable to load your user data.\nPlease contact an administrator!");
            return;
        }

        Player player = Bukkit.getPlayer(e.getUniqueId());
        if ( player != null ) {
            plugin.log("Player " + e.getName() + " joined before their data was loaded, this is very very bad! Please contact the developer!", Level.SEVERE);
        }

        unregisterdPlayers.put(e.getUniqueId(), register);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onLogin2(AsyncPlayerPreLoginEvent e) {
        if ( e.getLoginResult() == AsyncPlayerPreLoginEvent.Result.ALLOWED ) {
            return;
        }
        unregisterdPlayers.remove(e.getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent e) {
        unregisterdPlayers.remove(e.getPlayer().getUniqueId());

        PlatformPlayer player = plugin.getKdc().getPlayer(e.getPlayer().getUniqueId());
        if ( player != null ) {
            plugin.getKdc().onQuit(player);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent e) {
        UUID id = e.getPlayer().getUniqueId();
        if ( !unregisterdPlayers.containsKey(id) ) {
            return;
        }

        PlatformPlayer pp = new BukkitPlayer(e.getPlayer());
        unregisterdPlayers.remove(id).accept(pp);
        plugin.getKdc().getEventManager().dispatch(new PlayerLoadedEvent(pp));
    }

}
