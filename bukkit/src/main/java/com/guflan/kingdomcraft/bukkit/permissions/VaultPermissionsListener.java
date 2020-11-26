package com.guflan.kingdomcraft.bukkit.permissions;

import com.guflan.kingdomcraft.api.domain.Kingdom;
import com.guflan.kingdomcraft.api.domain.Rank;
import com.guflan.kingdomcraft.api.entity.PlatformPlayer;
import com.guflan.kingdomcraft.api.event.EventListener;
import com.guflan.kingdomcraft.bukkit.KingdomCraftBukkitPlugin;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class VaultPermissionsListener implements Listener, EventListener {

    private final KingdomCraftBukkitPlugin plugin;
    private final Permission permissionProvider;

    private final List<String> allExternals = new ArrayList<>();

    public VaultPermissionsListener(KingdomCraftBukkitPlugin plugin, Permission permissionProvider) {
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
    public void onKingdomJoin(PlatformPlayer player) {
        update(player);
    }

    @Override
    public void onKingdomLeave(PlatformPlayer player, Kingdom oldKingdom) {
        update(player);
    }

    @Override
    public void onRankChange(PlatformPlayer player, Rank oldRank) {
        update(player);
    }

    private void loadExternals() {
        allExternals.clear();
        plugin.getKdc().getPermissionManager().getGroups().forEach(group -> {
            allExternals.addAll(group.getExternals());
        });
    }

    private void update(PlatformPlayer player) {
        Player bplayer = Bukkit.getPlayer(player.getUniqueId());

        List<String> externals = new ArrayList<>();
        plugin.getKdc().getPermissionManager().getGroups().forEach(group
                -> externals.addAll(group.getExternals()));

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
