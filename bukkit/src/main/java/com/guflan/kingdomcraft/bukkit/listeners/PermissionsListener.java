package com.guflan.kingdomcraft.bukkit.listeners;

import com.guflan.kingdomcraft.api.domain.Kingdom;
import com.guflan.kingdomcraft.api.domain.Rank;
import com.guflan.kingdomcraft.api.domain.User;
import com.guflan.kingdomcraft.api.entity.PlatformPlayer;
import com.guflan.kingdomcraft.api.entity.PlatformSender;
import com.guflan.kingdomcraft.api.event.EventListener;
import com.guflan.kingdomcraft.bukkit.KingdomCraftBukkitPlugin;
import com.guflan.kingdomcraft.bukkit.entity.BukkitPlayer;
import com.guflan.kingdomcraft.common.command.CommandBaseImpl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.permissions.PermissionAttachment;

import java.util.Map;
import java.util.stream.Collectors;

public class PermissionsListener implements Listener, EventListener {

    private final static String PERMISSIONS_KEY = "permission_attachement";

    private final KingdomCraftBukkitPlugin plugin;

    public PermissionsListener(KingdomCraftBukkitPlugin plugin) {
        this.plugin = plugin;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        plugin.getKdc().getEventManager().addListener(this);

        plugin.getKdc().getCommandManager().registerCommand(new CommandBaseImpl(plugin.getKdc(), "perms", 0, true) {
            @Override
            public void execute(PlatformSender sender, String[] args) {
                PlatformPlayer p = (PlatformPlayer) sender;
                if ( !p.has(PERMISSIONS_KEY) ) {
                    sender.sendMessage("none");
                    return;
                }

                PermissionAttachment pa = p.get(PERMISSIONS_KEY, PermissionAttachment.class);
                sender.sendMessage(plugin.getKdc().getMessageManager().colorify(pa.getPermissions()
                        .keySet().stream()
                        .map(key -> pa.getPermissions().get(key) ? "&a" + key : "&c" + key)
                        .collect(Collectors.joining(", "))));
            }
        });
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
            clear(plugin.getKdc().getPlayer(e.getPlayer().getUniqueId()));
            return;
        }

        if ( toEnabled ) {
            update(plugin.getKdc().getPlayer(e.getPlayer().getUniqueId()));
        }
    }

    @Override
    public void onJoin(PlatformPlayer player) {
        update(player);
    }

    @Override
    public void onRankChange(PlatformPlayer player, Rank oldRank) {
        update(player);
    }

    @Override
    public void onKingdomLeave(PlatformPlayer player, Kingdom oldKingdom) {
        update(player);
    }

    @Override
    public void onKingdomJoin(PlatformPlayer player) {
        update(player);
    }

    //

    void clear(PlatformPlayer player) {
        if ( !player.has(PERMISSIONS_KEY) ) {
            return;
        }

        PermissionAttachment pa = player.get(PERMISSIONS_KEY, PermissionAttachment.class);
        Player p = ((BukkitPlayer) player).getPlayer();
        p.removeAttachment(pa);
    }

    void update(PlatformPlayer player) {
        clear(player);

        User user = plugin.getKdc().getUser(player);
        Map<String, Boolean> permissions = plugin.getKdc().getPermissionManager().getTotalPermissions(user);

        Player p = ((BukkitPlayer) player).getPlayer();
        PermissionAttachment pa = p.addAttachment(plugin);
        permissions.forEach(pa::setPermission);
        player.set(PERMISSIONS_KEY, pa);
    }

}
