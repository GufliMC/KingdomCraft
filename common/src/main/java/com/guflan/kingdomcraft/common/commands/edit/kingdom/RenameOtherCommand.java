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

package com.guflan.kingdomcraft.common.commands.edit.kingdom;

import com.guflan.kingdomcraft.api.domain.Kingdom;
import com.guflan.kingdomcraft.api.entity.PlatformPlayer;
import com.guflan.kingdomcraft.api.entity.PlatformSender;
import com.guflan.kingdomcraft.common.KingdomCraftImpl;
import com.guflan.kingdomcraft.common.command.CommandBase;

import java.util.List;
import java.util.stream.Collectors;

public class RenameOtherCommand extends CommandBase {

    public RenameOtherCommand(KingdomCraftImpl kdc) {
        super(kdc, "rename", 2);
        setArgumentsHint("<kingdom> <name>");
        setExplanationMessage("cmdRenameOtherExplanation");
        setPermissions("kingdom.rename.other");
    }

    @Override
    public List<String> autocomplete(PlatformPlayer sender, String[] args) {
        if ( args.length == 1 ) {
            return kdc.getKingdoms().stream().map(Kingdom::getName).collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public void execute(PlatformSender sender, String[] args) {
        Kingdom kingdom = kdc.getKingdom(args[0]);
        if ( kingdom == null ) {
            kdc.getMessageManager().send(sender, "cmdErrorKingdomNotExist", args[0]);
            return;
        }

        if ( !args[1].matches("[a-zA-Z0-9]+") ) {
            kdc.getMessageManager().send(sender, "cmdErrorInvalidName");
            return;
        }

        if ( kdc.getKingdom(args[1]) != null ) {
            kdc.getMessageManager().send(sender, "cmdErrorKingdomAlreadyExists", args[1]);
            return;
        }

        String oldName = kingdom.getName();
        kingdom.renameTo(args[1]);
        kdc.saveAsync(kingdom);

        kdc.getPlugin().getScheduler().executeSync(() ->
                kdc.getMessageManager().send(sender, "cmdRenameOther", oldName, kingdom.getName()));
    }
}
