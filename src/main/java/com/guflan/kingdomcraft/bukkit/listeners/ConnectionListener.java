package com.guflan.kingdomcraft.bukkit.listeners;

import com.guflan.kingdomcraft.api.KingdomCraftPlugin;
import com.guflan.kingdomcraft.api.domain.Player;
import com.guflan.kingdomcraft.api.entity.EntityPlayer;
import com.guflan.kingdomcraft.bukkit.entity.BukkitEntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ConnectionListener implements Listener {

    private final KingdomCraftPlugin plugin;

    public ConnectionListener(KingdomCraftPlugin plugin) {
        this.plugin = plugin;

        for ( org.bukkit.entity.Player bplayer : Bukkit.getOnlinePlayers() ) {
            join(bplayer);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLogin(PlayerLoginEvent e) {
        join(e.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent e) {
        plugin.getPlayerManager().leave(plugin.getPlayerManager().getOnlinePlayer(e.getPlayer().getUniqueId()));
    }

    private void join(org.bukkit.entity.Player bplayer) {
        Player player = plugin.getPlayerManager().getPlayer(bplayer.getUniqueId(), bplayer.getName());
        EntityPlayer eplayer = new BukkitEntityPlayer(bplayer, player);
        plugin.getPlayerManager().join(eplayer);
    }

}
