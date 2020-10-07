package com.guflan.kingdomcraft.bukkit.listeners;

import com.guflan.kingdomcraft.api.KingdomCraft;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ConnectionListener implements Listener {

    private final KingdomCraft kdc;

    public ConnectionListener(KingdomCraft kdc) {
        this.kdc = kdc;

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
        kdc.quit(kdc.getPlayer(e.getPlayer().getUniqueId()));
    }

    private void join(Player player) {
        kdc.join(kdc.getPlayer(player.getUniqueId()));
    }

}
