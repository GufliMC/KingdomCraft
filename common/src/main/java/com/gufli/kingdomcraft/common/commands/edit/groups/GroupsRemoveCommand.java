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
import com.gufli.kingdomcraft.api.domain.User;
import com.gufli.kingdomcraft.api.entity.PlatformPlayer;
import com.gufli.kingdomcraft.api.entity.PlatformSender;
import com.gufli.kingdomcraft.common.KingdomCraftImpl;
import com.gufli.kingdomcraft.common.command.CommandBase;
import com.gufli.kingdomcraft.common.permissions.PermissionGroup;

import java.util.List;
import java.util.stream.Collectors;

public class GroupsRemoveCommand extends CommandBase {

    public GroupsRemoveCommand(KingdomCraftImpl kdc) {
        super(kdc, "groups remove", 2, true);
        setArgumentsHint("<rank> <group>");
        setExplanationMessage(() -> kdc.getMessages().getMessage("cmdGroupsRemoveExplanation"));
        setPermissions("kingdom.groups.remove");
    }

    @Override
    public List<String> autocomplete(PlatformPlayer player, String[] args) {
        if ( args.length == 1 ) {
            User user = kdc.getUser(player);
            if ( user.getKingdom() == null ) {
                return null;
            }
            return user.getKingdom().getRanks().stream().map(Rank::getName).collect(Collectors.toList());
        }
        if ( args.length == 2 ) {
            User user = kdc.getUser(player);
            if ( user.getKingdom() == null || user.getKingdom().getRank(args[0]) == null ) {
                return null;
            }
            return user.getKingdom().getRank(args[0]).getPermissionGroups().stream()
                    .map(RankPermissionGroup::getName).collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public void execute(PlatformSender sender, String[] args) {
        User user = kdc.getUser((PlatformPlayer) sender);
        Kingdom kingdom = user.getKingdom();
        if ( kingdom == null ) {
            kdc.getMessages().send(sender, "cmdErrorSenderNoKingdom");
            return;
        }

        Rank rank = kingdom.getRank(args[0]);
        if ( rank == null ) {
            kdc.getMessages().send(sender, "cmdErrorRankNotExist", args[0]);
            return;
        }

        PermissionGroup group = kdc.getPermissionManager().getGroup(args[1]);
        if ( group == null ) {
            kdc.getMessages().send(sender, "cmdErrorPermissionGroupNotExist", args[1]);
            return;
        }

        RankPermissionGroup rpg = rank.getPermissionGroup(group.getName());
        if ( rpg == null ) {
            kdc.getMessages().send(sender, "cmdGroupsRemoveNotExist", group.getName(), rank.getName());
            return;
        }

        kdc.deleteAsync(rpg);
        kdc.getMessages().send(sender, "cmdGroupsRemove", group.getName(), rank.getName());
    }
}
