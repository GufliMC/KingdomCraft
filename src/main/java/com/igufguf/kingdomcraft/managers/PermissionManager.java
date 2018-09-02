package com.igufguf.kingdomcraft.managers;

import com.igufguf.kingdomcraft.KingdomCraft;
import com.igufguf.kingdomcraft.api.KingdomCraftApi;
import com.igufguf.kingdomcraft.commands.admin.DebugCommand;
import com.igufguf.kingdomcraft.api.models.kingdom.Kingdom;
import com.igufguf.kingdomcraft.api.models.kingdom.KingdomRank;
import com.igufguf.kingdomcraft.api.models.kingdom.KingdomUser;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionAttachment;

import java.util.Map;

/**
 * Copyrighted 2018 iGufGuf
 *
 * This file is part of KingdomCraft.
 *
 * Kingdomcraft is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * KingdomCraft is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with KingdomCraft.  If not, see <http://www.gnu.org/licenses/>.
 *
 **/
public class PermissionManager {

    private final KingdomCraft plugin;

    public PermissionManager(KingdomCraft plugin) {
        this.plugin = plugin;

        DebugCommand debugger = (DebugCommand) plugin.getApi().getCommandHandler().getByCommand("debug");
        debugger.register(new DebugCommand.DebugExecutor("perms") {
            @Override
            public void onExecute(CommandSender sender, String[] args) {
                if ( args.length == 0 ) return;

                KingdomUser user = plugin.getApi().getUserHandler().getUser(args[0]);
                if ( user == null ){
                    sender.sendMessage(ChatColor.RED + "User cannot be found!");
                    return;
                }

                String s = "";
                for ( String perm : user.getPermissions().getPermissions().keySet() ) {
                    s += ", " + ChatColor.DARK_GRAY + perm + " " + ChatColor.DARK_GRAY + "(" + ChatColor.GRAY + user.getPermissions().getPermissions().get(perm) + ChatColor.DARK_GRAY + ")";
                }

                if ( !s.equals("") ) {
                    s = s.substring(2);
                    sender.sendMessage(s);
                } else {
                    sender.sendMessage("none");
                }
            }
        });
    }

    public void refresh(KingdomUser user) {
        clear(user);

        if ( !plugin.getApi().isWorldEnabled(user.getPlayer().getWorld()) ) return;

        KingdomRank rank = plugin.getApi().getUserHandler().getRank(user);
        if ( rank == null ) return;

        PermissionAttachment pa = user.getPlayer().addAttachment(plugin);
        setup(pa, rank);

        user.setPermissions(pa);
    }

    public void clear(KingdomUser user) {
        PermissionAttachment pa = user.getPermissions();
        if ( pa == null ) return;

        pa.remove();
    }

    private void setup(PermissionAttachment pa, KingdomRank rank) {
        setup(pa, rank, false);
    }

    private void setup(PermissionAttachment pa, KingdomRank rank, boolean inverse) {
        Kingdom ko = rank.getKingdom();

        for ( Map.Entry<String, Boolean> perm : rank.getPermissions().entrySet() ) {

            // inheritances, if the permission is a rank name, setup the permissions for that rank
            KingdomRank r = ko.getRank(perm.getKey());
            if ( r != null && r != rank) {
                setup(pa, r, !perm.getValue());
                continue;
            }

            pa.setPermission(perm.getKey(), inverse ? !perm.getValue() : perm.getValue());
        }
    }

}
