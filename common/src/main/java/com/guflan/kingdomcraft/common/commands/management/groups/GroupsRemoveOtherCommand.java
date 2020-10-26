/*
 * This file is part of KingdomCraft.
 *
 * KingdomCraft is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * KingdomCraft is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with KingdomCraft. If not, see <https://www.gnu.org/licenses/>.
 */

package com.guflan.kingdomcraft.common.commands.management.groups;

import com.guflan.kingdomcraft.api.domain.Kingdom;
import com.guflan.kingdomcraft.api.domain.Rank;
import com.guflan.kingdomcraft.api.domain.RankPermissionGroup;
import com.guflan.kingdomcraft.api.entity.PlatformSender;
import com.guflan.kingdomcraft.common.KingdomCraftImpl;
import com.guflan.kingdomcraft.common.command.CommandBase;
import com.guflan.kingdomcraft.common.permissions.PermissionGroup;

public class GroupsRemoveOtherCommand extends CommandBase {

    public GroupsRemoveOtherCommand(KingdomCraftImpl kdc) {
        super(kdc, "groups remove", 3);
        setArgumentsHint("<kingdom> <rank> <group>");
        setExplanationMessage(kdc.getMessageManager().getMessage("cmdGroupsRemoveOtherExplanation"));
        setPermissions("kingdom.groups.remove.other");
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

        // async saving
        kdc.getPlugin().getScheduler().executeAsync(rpg::delete);

        kdc.getMessageManager().send(sender, "cmdGroupsRemoveOtherSuccess", group.getName(), rank.getName(), kingdom.getName());
    }
}
