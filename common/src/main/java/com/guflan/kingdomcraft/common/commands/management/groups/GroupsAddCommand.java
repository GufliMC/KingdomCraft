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
import com.guflan.kingdomcraft.api.domain.User;
import com.guflan.kingdomcraft.api.entity.PlatformPlayer;
import com.guflan.kingdomcraft.api.entity.PlatformSender;
import com.guflan.kingdomcraft.common.KingdomCraftImpl;
import com.guflan.kingdomcraft.common.command.CommandBaseImpl;
import com.guflan.kingdomcraft.common.permissions.PermissionGroup;

public class GroupsAddCommand extends CommandBaseImpl {

    public GroupsAddCommand(KingdomCraftImpl kdc) {
        super(kdc, "groups add", 2, true);
        setArgumentsHint("<rank> <group>");
        setExplanationMessage(kdc.getMessageManager().getMessage("cmdGroupsAddExplanation"));
        setPermissions("kingdom.groups.add");
    }

    @Override
    public void execute(PlatformSender sender, String[] args) {
        User user = kdc.getUser((PlatformPlayer) sender);
        Kingdom kingdom = user.getKingdom();
        if ( kingdom == null ) {
            kdc.getMessageManager().send(sender, "cmdErrorSenderNoKingdom");
            return;
        }

        Rank rank = kingdom.getRank(args[0]);
        if ( rank == null ) {
            kdc.getMessageManager().send(sender, "cmdErrorRankNotExist", args[0]);
            return;
        }

        PermissionGroup group = kdc.getPermissionManager().getGroup(args[1]);
        if ( group == null ) {
            kdc.getMessageManager().send(sender, "cmdErrorPermissionGroupNotExist", args[1]);
            return;
        }

        if ( rank.getPermissionGroup(group.getName()) != null ) {
            kdc.getMessageManager().send(sender, "cmdGroupsAddAlreadyExists", group.getName(), rank.getName());
            return;
        }

        RankPermissionGroup rpg = rank.createPermissionGroup(group.getName());

        // async saving
        kdc.getPlugin().getScheduler().executeAsync(rpg::save);

        kdc.getMessageManager().send(sender, "cmdGroupsAddSuccess", group.getName(), rank.getName());
    }
}
