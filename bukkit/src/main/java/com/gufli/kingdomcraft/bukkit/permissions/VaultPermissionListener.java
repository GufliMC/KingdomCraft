package com.gufli.kingdomcraft.bukkit.permissions;

import com.gufli.kingdomcraft.api.domain.Rank;
import com.gufli.kingdomcraft.api.domain.User;
import com.gufli.kingdomcraft.api.entity.PlatformPlayer;
import com.gufli.kingdomcraft.api.events.*;
import com.gufli.kingdomcraft.bukkit.KingdomCraftBukkitPlugin;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class VaultPermissionListener implements Listener {

    private final KingdomCraftBukkitPlugin plugin;
    private final Permission permissionProvider;

    private final List<String> allExternals = new ArrayList<>();

    public VaultPermissionListener(KingdomCraftBukkitPlugin plugin, Permission permissionProvider) {
        this.plugin = plugin;
        this.permissionProvider = permissionProvider;
        loadExternals();

        plugin.getKdc().getEventManager().addListener(PluginReloadEvent.class, this::onReload);
        plugin.getKdc().getEventManager().addListener(PlayerLoadedEvent.class, this::onJoin);
        plugin.getKdc().getEventManager().addListener(PlayerLeaveEvent.class, this::onLeave);
        plugin.getKdc().getEventManager().addListener(UserJoinKingdomEvent.class, this::onKingdomJoin);
        plugin.getKdc().getEventManager().addListener(UserLeaveKingdomEvent.class, this::onKingdomLeave);
        plugin.getKdc().getEventManager().addListener(UserRankChangeEvent.class, this::onRankChange);
    }

    public void onReload(PluginReloadEvent e) {
        loadExternals();
    }

    public void onJoin(PlayerLoadedEvent e) {
        update(e.getPlayer());
    }

    public void onLeave(PlayerLeaveEvent e) {
        update(e.getPlayer());
    }

    public void onKingdomJoin(UserJoinKingdomEvent e) {
        update(e.getUser());
    }

    public void onKingdomLeave(UserLeaveKingdomEvent e) {
        update(e.getUser());
    }

    public void onRankChange(UserRankChangeEvent e) {
        update(e.getUser());
    }

    private void loadExternals() {
        allExternals.clear();
        plugin.getKdc().getPermissionManager().getGroups().forEach(group -> {
            allExternals.addAll(group.getExternals());
        });
    }

    private void update(User user) {
        PlatformPlayer player = plugin.getKdc().getPlayer(user);
        if ( player != null ) {
            update(player);
        }
    }

    private void update(PlatformPlayer player) {
        Player bplayer = Bukkit.getPlayer(player.getUniqueId());

        List<String> externals = new ArrayList<>();
        Rank rank = player.getUser().getRank();
        if ( rank != null ) {
            plugin.getKdc().getPermissionManager().getGroups(rank).forEach(group
                    -> externals.addAll(group.getExternals()));
        }

        List<String> distinctExternals = new ArrayList<>(allExternals);
        distinctExternals.removeIf(externals::contains);

        for ( String group : distinctExternals ) {
            if ( permissionProvider.playerInGroup(bplayer, group) ) {
                permissionProvider.playerRemoveGroup(bplayer, group);
            }
        }

        for ( String group : externals ) {
            if ( !permissionProvider.playerInGroup(bplayer, group) ) {
                permissionProvider.playerAddGroup(bplayer, group);
            }
        }
    }

}
