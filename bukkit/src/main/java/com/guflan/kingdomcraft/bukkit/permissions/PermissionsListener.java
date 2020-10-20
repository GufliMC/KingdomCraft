package com.guflan.kingdomcraft.bukkit.permissions;

import com.guflan.kingdomcraft.api.domain.Kingdom;
import com.guflan.kingdomcraft.api.domain.Rank;
import com.guflan.kingdomcraft.api.entity.PlatformPlayer;
import com.guflan.kingdomcraft.api.event.EventListener;
import com.guflan.kingdomcraft.bukkit.KingdomCraftBukkitPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PermissionsListener implements Listener, EventListener {

    private final KingdomCraftBukkitPlugin plugin;
    private final PermissionHandler handler;

    public PermissionsListener(KingdomCraftBukkitPlugin plugin, PermissionHandler handler) {
        this.plugin = plugin;
        this.handler = handler;

        plugin.getKdc().getEventManager().addListener(this);

        for (PlatformPlayer p : plugin.getKdc().getOnlinePlayers()) {
            handler.update(p);
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {
        if ( e.getFrom().getWorld() == e.getTo().getWorld() ) {
            return;
        }

        boolean fromEnabled = plugin.getKdc().getConfig().isWorldEnabled(e.getFrom().getWorld().getName());
        boolean toEnabled = plugin.getKdc().getConfig().isWorldEnabled(e.getTo().getWorld().getName());

        if ( fromEnabled && toEnabled ) {
            return;
        }

        if ( fromEnabled ) {
            handler.clear(plugin.getKdc().getPlayer(e.getPlayer().getUniqueId()));
            return;
        }

        if ( toEnabled ) {
            handler.update(plugin.getKdc().getPlayer(e.getPlayer().getUniqueId()));
        }
    }

    @Override
    public void onJoin(PlatformPlayer player) {
        handler.update(player);
    }

    @Override
    public void onRankChange(PlatformPlayer player, Rank oldRank) {
        handler.update(player);
    }

    @Override
    public void onKingdomLeave(PlatformPlayer player, Kingdom oldKingdom) {
        handler.update(player);
    }

    @Override
    public void onKingdomJoin(PlatformPlayer player) {
        handler.update(player);
    }

}
