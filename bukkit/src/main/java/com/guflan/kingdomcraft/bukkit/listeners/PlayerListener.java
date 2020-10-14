package com.guflan.kingdomcraft.bukkit.listeners;

import com.guflan.kingdomcraft.api.KingdomCraft;
import com.guflan.kingdomcraft.api.domain.models.Kingdom;
import com.guflan.kingdomcraft.api.entity.Player;
import com.guflan.kingdomcraft.api.event.EventListener;
import com.guflan.kingdomcraft.bukkit.BukkitKingdomCraftPlugin;
import com.guflan.kingdomcraft.bukkit.entity.BukkitPlayer;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.List;

public class PlayerListener implements Listener, EventListener {

    private final BukkitKingdomCraftPlugin plugin;

    public PlayerListener(BukkitKingdomCraftPlugin plugin) {
        this.plugin = plugin;
        KingdomCraft.getEventManager().addListener(this);
    }

    // join & quit messages

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if ( plugin.getConfiguration().getOnJoinMessage() != null
                && !plugin.getConfiguration().getOnJoinMessage().equals("") ) {
            e.setJoinMessage(null);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent e) {
        Player player = KingdomCraft.getPlayer(e.getPlayer().getUniqueId());
        String msg = plugin.getConfiguration().getOnLeaveMessage();
        msg = KingdomCraft.getPlaceholderManager().handle(player, msg);
        e.setQuitMessage(msg);
    }

    @Override
    public void onJoin(Player player) {
        String msg = plugin.getConfiguration().getOnJoinMessage();
        msg = KingdomCraft.getPlaceholderManager().handle(player, msg);
        Bukkit.broadcastMessage(msg);
    }

    // respawn

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        if ( plugin.getConfiguration().respawnAtKingdom() ) {
            // TODO
        }
    }

    // kingdom join & kingdom leave commands

    @Override
    public void onKingdomJoin(Player player) {
        if ( plugin.getConfiguration().getOnKingdomJoinCommands().isEmpty() ) {
            return;
        }
        execute(player, plugin.getConfiguration().getOnKingdomJoinCommands());
    }

    @Override
    public void onKingdomLeave(Player player, Kingdom oldKingdom) {
        if ( plugin.getConfiguration().getOnKingdomLeaveCommands().isEmpty() ) {
            return;
        }
        execute(player, plugin.getConfiguration().getOnKingdomLeaveCommands());
    }

    private void execute(Player player, List<String> commands) {
        if ( !(player instanceof BukkitPlayer) ) {
            return;
        }
        org.bukkit.entity.Player bplayer = ((BukkitPlayer) player).getPlayer();

        for ( String cmd : commands ) {
            cmd = KingdomCraft.getPlaceholderManager().handle(player, cmd);

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
            if ( plugin.getConfiguration().getOnKillMessage() == null || plugin.getConfiguration().getOnKillMessage().equals("") ) {
                return;
            }

            Player p = KingdomCraft.getPlayer(event.getEntity().getUniqueId());
            Player k = KingdomCraft.getPlayer(event.getEntity().getKiller().getUniqueId());

            String msg = plugin.getConfiguration().getOnKillMessage();
            msg = KingdomCraft.getPlaceholderManager().handle(p, msg);
            msg = KingdomCraft.getPlaceholderManager().handle(k, msg, "killer_");
            event.setDeathMessage(msg);
            return;
        }

        if ( plugin.getConfiguration().getOnDeathMessage() == null || plugin.getConfiguration().getOnDeathMessage().equals("") ) {
            return;
        }

        Player p = KingdomCraft.getPlayer(event.getEntity().getUniqueId());

        String msg = plugin.getConfiguration().getOnDeathMessage();
        msg = KingdomCraft.getPlaceholderManager().handle(p, msg);
        event.setDeathMessage(msg);
    }

}
