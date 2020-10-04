package com.guflan.kingdomcraft.bukkit.listeners;

import com.guflan.kingdomcraft.api.domain.User;
import com.guflan.kingdomcraft.bukkit.KingdomCraft;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ConnectionListener implements Listener {

    private final KingdomCraft plugin;

    public ConnectionListener(KingdomCraft plugin) {
        this.plugin = plugin;

        for ( Player player : Bukkit.getOnlinePlayers() ) {
            join(player);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLogin(PlayerLoginEvent e) {
        join(e.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent e) {
        User user = plugin.getBridge().getUserManager().getUser(e.getPlayer().getUniqueId());
        plugin.getBridge().quit(user);
    }

    private void join(Player player) {
        User user = plugin.getBridge().getUserManager().getUser(player.getUniqueId());
        plugin.getBridge().join(user);
    }

}
