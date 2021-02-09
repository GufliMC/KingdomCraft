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

import com.gufli.kingdomcraft.api.entity.PlatformPlayer;
import com.gufli.kingdomcraft.api.entity.PlatformSender;
import com.gufli.kingdomcraft.common.KingdomCraftImpl;
import com.gufli.kingdomcraft.common.command.CommandBase;
import com.gufli.kingdomcraft.common.permissions.PermissionGroup;

import java.util.List;
import java.util.stream.Collectors;

public class GroupsCommand extends CommandBase {

    public GroupsCommand(KingdomCraftImpl kdc) {
        super(kdc, "groups", 0);
        setExplanationMessage("cmdGroupsExplanation");
        setPermissions("kingdom.groups");
    }

    @Override
    public List<String> autocomplete(PlatformPlayer player, String[] args) {
        return null;
    }

    @Override
    public void execute(PlatformSender sender, String[] args) {

        List<String> groups = kdc.getPermissionManager().getGroups().stream()
                .map(PermissionGroup::getName).collect(Collectors.toList());

        String delimiter = kdc.getMessages().getMessage("cmdGroupsDelimiter");
        delimiter = delimiter != null ? delimiter : "&a, &2";

        String result = String.join(delimiter, groups);
        kdc.getMessages().send(sender, "cmdGroups", result);
    }
}
