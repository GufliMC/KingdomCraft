package com.guflan.kingdomcraft.bukkit.listeners;

import com.guflan.kingdomcraft.api.entity.PlatformPlayer;
import com.guflan.kingdomcraft.api.event.EventListener;
import com.guflan.kingdomcraft.bukkit.KingdomCraftBukkitPlugin;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
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
