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

package com.gufli.kingdomcraft.common.commands.edit.kingdom;

import com.gufli.kingdomcraft.api.domain.Kingdom;
import com.gufli.kingdomcraft.api.entity.PlatformPlayer;
import com.gufli.kingdomcraft.api.entity.PlatformSender;
import com.gufli.kingdomcraft.common.KingdomCraftImpl;
import com.gufli.kingdomcraft.common.command.CommandBase;

import java.util.List;
import java.util.stream.Collectors;

public class EditMaxMembersOtherCommand extends CommandBase {

    public EditMaxMembersOtherCommand(KingdomCraftImpl kdc) {
        super(kdc, "edit max-members", 2);
        setArgumentsHint("<kingdom> <amount>");
        setExplanationMessage(() -> kdc.getMessages().getMessage("cmdExitMaxMembersOtherExplanation"));
        setPermissions("kingdom.edit.max-members.other");
    }

    @Override
    public List<String> autocomplete(PlatformPlayer sender, String[] args) {
        if ( args.length == 1 ) {
            return kdc.getKingdoms(true).stream().map(Kingdom::getName).collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public void execute(PlatformSender sender, String[] args) {
        Kingdom kingdom = kdc.getKingdom(args[0]);
        if ( kingdom == null ) {
            kdc.getMessages().send(sender, "cmdErrorKingdomNotExist", args[0]);
            return;
        }

        if ( !args[1].matches("[0-9]+") ) {
            kdc.getMessages().send(sender, "cmdErrorInvalidNumber", args[1]);
            return;
        }

        kingdom.setMaxMembers(Integer.parseInt(args[1]));
        kdc.saveAsync(kingdom);

        kdc.getMessages().send(sender, "cmdEditOther", "max-members", kingdom.getName(), kingdom.getMaxMembers() + "");
    }
}
