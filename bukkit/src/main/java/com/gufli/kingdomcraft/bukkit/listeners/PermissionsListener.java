/*
 * This file is part of KingdomCraft.
 *
 * KingdomCraft is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * KingdomCraft is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with KingdomCraft. If not, see <https://www.gnu.org/licenses/>.
 */

package com.gufli.kingdomcraft.bukkit.listeners;

import com.gufli.kingdomcraft.api.domain.User;
import com.gufli.kingdomcraft.api.entity.PlatformPlayer;
import com.gufli.kingdomcraft.api.events.*;
import com.gufli.kingdomcraft.bukkit.KingdomCraftBukkitPlugin;
import com.gufli.kingdomcraft.bukkit.entity.BukkitPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.permissions.PermissionAttachment;

import java.util.Map;
import java.util.logging.Level;

public class PermissionsListener implements Listener {

    private final static String PERMISSIONS_KEY = "permission_attachement";

    private final KingdomCraftBukkitPlugin plugin;

    public PermissionsListener(KingdomCraftBukkitPlugin plugin) {
        this.plugin = plugin;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        plugin.getKdc().getEventManager().addListener(PlayerLoadedEvent.class, this::onJoin);
        plugin.getKdc().getEventManager().addListener(UserRankChangeEvent.class, this::onRankChange);
        plugin.getKdc().getEventManager().addListener(UserLeaveKingdomEvent.class, this::onKingdomLeave);
        plugin.getKdc().getEventManager().addListener(UserJoinKingdomEvent.class, this::onKingdomJoin);
        plugin.getKdc().getEventManager().addListener(PluginReloadEvent.class, this::onReload);

        plugin.getKdc().getOnlinePlayers().forEach(this::update);

//        // DEBUG TOOL
//        plugin.getKdc().getCommandManager().addCommand(new CommandBase(plugin.getKdc(), "perms", 0, true) {
//            @Override
//            public void execute(PlatformSender sender, String[] args) {
//                PlatformPlayer p = (PlatformPlayer) sender;
//                if ( !p.has(PERMISSIONS_KEY) ) {
//                    sender.sendMessage("none");
//                    return;
//                }
//
//                PermissionAttachment pa = p.get(PERMISSIONS_KEY, PermissionAttachment.class);
//                sender.sendMessage(plugin.getKdc().getMessageManager().colorify(pa.getPermissions()
//                        .keySet().stream()
//                        .map(key -> pa.getPermissions().get(key) ? "&a" + key : "&c" + key)
//                        .collect(Collectors.joining(", "))));
//            }
//        });
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

    public void onJoin(PlayerLoadedEvent e) {
        update(e.getPlayer());
    }

    public void onRankChange(UserRankChangeEvent e) {
        update(e.getUser());
    }

    public void onKingdomLeave(UserLeaveKingdomEvent e) {
        update(e.getUser());
    }

    public void onKingdomJoin(UserJoinKingdomEvent e) {
        update(e.getUser());
    }

    public void onReload(PluginReloadEvent e) {
        plugin.getKdc().getOnlinePlayers().forEach(this::update);
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

    private void update(User user) {
        PlatformPlayer player = plugin.getKdc().getPlayer(user);
        if ( player != null ) {
            update(player);
        }
    }

    void update(PlatformPlayer player) {
        clear(player);

        Map<String, Boolean> permissions = plugin.getKdc().getPermissionManager()
                .getTotalPermissions(player);

        Player p = ((BukkitPlayer) player).getPlayer();

        PermissionAttachment pa = p.addAttachment(plugin);
        if ( pa == null ) {
            plugin.log("Unable to set permissions for " + player.getName() + ".", Level.SEVERE);
            plugin.log("Plugin status: " + (plugin.isEnabled() ? "enabled" : "disabled"), Level.SEVERE);
            return;
        }

        for ( String perm : permissions.keySet() ) {
            pa.setPermission(perm, permissions.get(perm));
        }
        player.set(PERMISSIONS_KEY, pa);
    }

}
