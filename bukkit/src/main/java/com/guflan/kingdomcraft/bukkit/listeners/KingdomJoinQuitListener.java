package com.guflan.kingdomcraft.bukkit.listeners;

import com.guflan.kingdomcraft.api.domain.Kingdom;
import com.guflan.kingdomcraft.api.entity.PlatformPlayer;
import com.guflan.kingdomcraft.api.event.EventListener;
import com.guflan.kingdomcraft.bukkit.KingdomCraftBukkitPlugin;
import com.guflan.kingdomcraft.bukkit.entity.BukkitPlayer;
import org.bukkit.entity.Player;

import java.util.List;

public class KingdomJoinQuitListener implements EventListener {

    private final KingdomCraftBukkitPlugin plugin;

    public KingdomJoinQuitListener(KingdomCraftBukkitPlugin plugin) {
        this.plugin = plugin;
        plugin.getKdc().getEventManager().addListener(this);
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
}
