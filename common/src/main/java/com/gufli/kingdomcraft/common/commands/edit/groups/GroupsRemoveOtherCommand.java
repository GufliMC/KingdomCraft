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

package com.gufli.kingdomcraft.common.commands.edit.groups;

import com.gufli.kingdomcraft.api.domain.Kingdom;
import com.gufli.kingdomcraft.api.domain.Rank;
import com.gufli.kingdomcraft.api.domain.RankPermissionGroup;
import com.gufli.kingdomcraft.api.entity.PlatformPlayer;
import com.gufli.kingdomcraft.api.entity.PlatformSender;
import com.gufli.kingdomcraft.common.KingdomCraftImpl;
import com.gufli.kingdomcraft.common.command.CommandBase;
import com.gufli.kingdomcraft.common.permissions.PermissionGroup;

import java.util.List;
import java.util.stream.Collectors;

public class GroupsRemoveOtherCommand extends CommandBase {

    public GroupsRemoveOtherCommand(KingdomCraftImpl kdc) {
        super(kdc, "groups remove", 3);
        setArgumentsHint("<kingdom> <rank> <group>");
        setExplanationMessage("cmdGroupsRemoveOtherExplanation");
        setPermissions("kingdom.groups.remove.other");
    }

    @Override
    public List<String> autocomplete(PlatformPlayer player, String[] args) {
        if ( args.length == 1 ) {
            return kdc.getKingdoms().stream().map(Kingdom::getName).collect(Collectors.toList());
        }
        if ( args.length == 2 ) {
            Kingdom kingdom = kdc.getKingdom(args[0]);
            if ( kingdom == null ) {
                return null;
            }
            return kingdom.getRanks().stream().map(Rank::getName).collect(Collectors.toList());
        }
        if ( args.length == 3 ) {
            Kingdom kingdom = kdc.getKingdom(args[0]);
            if ( kingdom == null || kingdom.getRank(args[1]) == null) {
                return null;
            }
            return kingdom.getRank(args[1]).getPermissionGroups().stream()
                    .map(RankPermissionGroup::getName).collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public void execute(PlatformSender sender, String[] args) {
        Kingdom kingdom = kdc.getKingdom(args[0]);
        if ( kingdom == null ) {
            kdc.getMessageManager().send(sender, "cmdErrorSenderNoKingdom");
            return;
        }

        Rank rank = kingdom.getRank(args[1]);
        if ( rank == null ) {
            kdc.getMessageManager().send(sender, "cmdErrorRankNotExist", args[1]);
            return;
        }

        PermissionGroup group = kdc.getPermissionManager().getGroup(args[2]);
        if ( group == null ) {
            kdc.getMessageManager().send(sender, "cmdErrorPermissionGroupNotExist", args[2]);
            return;
        }

        RankPermissionGroup rpg = rank.getPermissionGroup(group.getName());
        if ( rpg == null ) {
            kdc.getMessageManager().send(sender, "cmdGroupsRemoveNotExist", group.getName(), rank.getName());
            return;
        }

        kdc.deleteAsync(rpg);
        kdc.getMessageManager().send(sender, "cmdGroupsRemoveOther", group.getName(), rank.getName(), kingdom.getName());
    }
}
