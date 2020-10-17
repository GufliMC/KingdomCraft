package com.guflan.kingdomcraft.bukkit.listeners;

import com.guflan.kingdomcraft.api.domain.Kingdom;
import com.guflan.kingdomcraft.api.entity.PlatformPlayer;
import com.guflan.kingdomcraft.api.event.EventListener;
import com.guflan.kingdomcraft.bukkit.KingdomCraftBukkitPlugin;
import com.guflan.kingdomcraft.bukkit.entity.BukkitPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.List;

public class PlayerListener implements Listener, EventListener {

    private final KingdomCraftBukkitPlugin plugin;

    public PlayerListener(KingdomCraftBukkitPlugin plugin) {
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

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent e) {
        PlatformPlayer player = plugin.getKdc().getPlayer(e.getPlayer().getUniqueId());
        String msg = plugin.getKdc().getConfig().getOnLeaveMessage();
        msg = plugin.getKdc().getPlaceholderManager().handle(player, msg);
        e.setQuitMessage(msg);
    }

    @Override
    public void onJoin(PlatformPlayer player) {
        String msg = plugin.getKdc().getConfig().getOnJoinMessage();
        msg = plugin.getKdc().getPlaceholderManager().handle(player, msg);
        Bukkit.broadcastMessage(msg);
    }

    // respawn

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        if ( plugin.getKdc().getConfig().respawnAtKingdom() ) {
            // TODO
        }
    }

    // kingdom join & kingdom leave commands

    @Override
    public void onKingdomJoin(PlatformPlayer player) {
        if ( plugin.getKdc().getConfig().getOnKingdomJoinCommands().isEmpty() ) {
            return;
        }
        execute(player, plugin.getKdc().getConfig().getOnKingdomJoinCommands());
    }

    @Override
    public void onKingdomLeave(PlatformPlayer player, Kingdom oldKingdom) {
        if ( plugin.getKdc().getConfig().getOnKingdomLeaveCommands().isEmpty() ) {
            return;
        }
        execute(player, plugin.getKdc().getConfig().getOnKingdomLeaveCommands());
    }

    private void execute(PlatformPlayer player, List<String> commands) {
        if ( !(player instanceof BukkitPlayer) ) {
            return;
        }
        Player bplayer = ((BukkitPlayer) player).getPlayer();

        for ( String cmd : commands ) {
            cmd = plugin.getKdc().getPlaceholderManager().handle(player, cmd);

            if ( cmd.startsWith("CONSOLE") ) {
                cmd = cmd.substring("CONSOLE".length()).trim();
                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), cmd);
            } else {
                bplayer.chat("/" + cmd);
            }
        }
    }

    // death messages

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if ( event.getEntity().getKiller() != null ) {
            if ( plugin.getKdc().getConfig().getOnKillMessage() == null
                    || plugin.getKdc().getConfig().getOnKillMessage().equals("") ) {
                return;
            }

            PlatformPlayer p = plugin.getKdc().getPlayer(event.getEntity().getUniqueId());
            PlatformPlayer k = plugin.getKdc().getPlayer(event.getEntity().getKiller().getUniqueId());

            String msg = plugin.getKdc().getConfig().getOnKillMessage();
            msg = plugin.getKdc().getPlaceholderManager().handle(p, msg);
            msg = plugin.getKdc().getPlaceholderManager().handle(k, msg, "killer_");
            event.setDeathMessage(msg);
            return;
        }

        if ( plugin.getKdc().getConfig().getOnDeathMessage() == null
                || plugin.getKdc().getConfig().getOnDeathMessage().equals("") ) {
            return;
        }

        PlatformPlayer p = plugin.getKdc().getPlayer(event.getEntity().getUniqueId());

        String msg = plugin.getKdc().getConfig().getOnDeathMessage();
        msg = plugin.getKdc().getPlaceholderManager().handle(p, msg);
        event.setDeathMessage(msg);
    }

}
