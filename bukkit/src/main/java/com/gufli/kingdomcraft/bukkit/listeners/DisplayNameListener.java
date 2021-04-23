package com.gufli.kingdomcraft.bukkit.listeners;

import com.gufli.kingdomcraft.api.domain.Kingdom;
import com.gufli.kingdomcraft.api.entity.PlatformPlayer;
import com.gufli.kingdomcraft.api.events.PlayerLoadedEvent;
import com.gufli.kingdomcraft.api.events.UserJoinKingdomEvent;
import com.gufli.kingdomcraft.api.events.UserLeaveKingdomEvent;
import com.gufli.kingdomcraft.bukkit.KingdomCraftBukkitPlugin;
import com.gufli.kingdomcraft.bukkit.entity.BukkitPlayer;
import org.bukkit.entity.Player;

public class DisplayNameListener {


    private final KingdomCraftBukkitPlugin plugin;

    public DisplayNameListener(KingdomCraftBukkitPlugin plugin) {
        this.plugin = plugin;

        plugin.getKdc().getEventManager().addListener(PlayerLoadedEvent.class, this::onLoad);
        plugin.getKdc().getEventManager().addListener(UserJoinKingdomEvent.class, this::onKingdomJoin);
        plugin.getKdc().getEventManager().addListener(UserLeaveKingdomEvent.class, this::onKingdomLeave);
    }

    // change displayname
    public void onLoad(PlayerLoadedEvent event) {
        update(event.getPlayer());
    }

    public void onKingdomJoin(UserJoinKingdomEvent event) {
        PlatformPlayer player = plugin.getKdc().getPlayer(event.getUser());
        if ( player != null ) {
            update(player);
        }
    }

    public void onKingdomLeave(UserLeaveKingdomEvent event) {
        PlatformPlayer player = plugin.getKdc().getPlayer(event.getUser());
        if ( player != null ) {
            update(player);
        }
    }

    private void update(PlatformPlayer pp) {
        if ( !plugin.getKdc().getConfig().updateDisplayNames() ) {
            return;
        }

        String prefix = plugin.getKdc().getPlaceholderManager().handle(pp, "{kingdom_prefix}");
        if ( !prefix.equals("") && !plugin.decolorify(prefix).endsWith(" ")) {
            prefix += " ";
        }

        String suffix = plugin.getKdc().getPlaceholderManager().handle(pp, "{kingdom_suffix}");
        if ( !suffix.equals("") && !plugin.decolorify(suffix).startsWith(" ")) {
            suffix = " "+ suffix;
        }

        Player player = ((BukkitPlayer) pp).getPlayer();

        if ( prefix.equals("") && suffix.equals("") ) {
            player.setDisplayName(player.getName());
            return;
        }

        player.setDisplayName(prefix + player.getName() + suffix);
    }

}
