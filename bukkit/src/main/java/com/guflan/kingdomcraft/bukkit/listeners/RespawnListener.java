package com.guflan.kingdomcraft.bukkit.listeners;

import com.guflan.kingdomcraft.bukkit.KingdomCraftBukkitPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class RespawnListener implements Listener {

    private final KingdomCraftBukkitPlugin plugin;

    public RespawnListener(KingdomCraftBukkitPlugin plugin) {
        this.plugin = plugin;
    }

    // respawn

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        if ( plugin.getKdc().getConfig().respawnAtKingdom() ) {
            // TODO
        }
    }
}
