package com.gufli.kingdomcraft.bukkit.permissions;

import com.gufli.kingdomcraft.api.domain.Kingdom;
import com.gufli.kingdomcraft.api.domain.Rank;
import com.gufli.kingdomcraft.api.domain.User;
import com.gufli.kingdomcraft.api.entity.PlatformPlayer;
import com.gufli.kingdomcraft.api.event.EventListener;
import com.gufli.kingdomcraft.bukkit.KingdomCraftBukkitPlugin;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class VaultPermissionListener implements Listener, EventListener {

    private final KingdomCraftBukkitPlugin plugin;
    private final Permission permissionProvider;

    private final List<String> allExternals = new ArrayList<>();

    public VaultPermissionListener(KingdomCraftBukkitPlugin plugin, Permission permissionProvider) {
        this.plugin = plugin;
        this.permissionProvider = permissionProvider;
        loadExternals();
    }

    @Override
    public void onReload() {
        loadExternals();
    }

    @Override
    public void onJoin(PlatformPlayer player) {
        update(player);
    }

    @Override
    public void onQuit(PlatformPlayer player) {
        update(player);
    }

    @Override
    public void onKingdomJoin(User user) {
        update(user);
    }

    @Override
    public void onKingdomLeave(User user, Kingdom oldKingdom) {
        update(user);
    }

    @Override
    public void onRankChange(User user, Rank oldRank) {
        update(user);
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
