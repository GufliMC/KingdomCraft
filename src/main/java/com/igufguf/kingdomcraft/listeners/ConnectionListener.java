package com.igufguf.kingdomcraft.listeners;

import com.igufguf.kingdomcraft.KingdomCraft;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ConnectionListener implements Listener {

    private final KingdomCraft kingdomCraft;

    public ConnectionListener(KingdomCraft kingdomCraft) {
        this.kingdomCraft = kingdomCraft;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLogin(PlayerLoginEvent e) {
        kingdomCraft.playerHandler.join(e.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent e) {
        kingdomCraft.playerHandler.quit(e.getPlayer());
    }
}
