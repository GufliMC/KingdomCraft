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

package com.guflan.kingdomcraft.common.commands.management.kingdom;

import com.guflan.kingdomcraft.api.domain.Kingdom;
import com.guflan.kingdomcraft.api.entity.PlatformSender;
import com.guflan.kingdomcraft.common.KingdomCraftImpl;
import com.guflan.kingdomcraft.common.command.CommandBaseImpl;

public class EditInviteOnlyOtherCommand extends CommandBaseImpl {

    public EditInviteOnlyOtherCommand(KingdomCraftImpl kdc) {
        super(kdc, "edit invite-only", 2);
        setArgumentsHint("<kingdom> [true/false]");
        setExplanationMessage(kdc.getMessageManager().getMessage("cmdInviteOnlyOtherExplanation"));
        setPermissions("kingdom.edit.invite-only.other");
    }

    @Override
    public void execute(PlatformSender sender, String[] args) {
        Kingdom kingdom = kdc.getKingdom(args[0]);
        if ( kingdom == null ) {
            kdc.getMessageManager().send(sender, "cmdErrorKingdomNotExist", args[0]);
            return;
        }

        if ( !args[1].equalsIgnoreCase("true")
                && !args[1].equalsIgnoreCase("false") ) {
            kdc.getMessageManager().send(sender, "errorInvalidBoolean", args[1]);
            return;
        }

        kingdom.setInviteOnly(Boolean.parseBoolean(args[1]));

        // async saving
        kdc.getPlugin().getScheduler().executeAsync(kingdom::save);

        kdc.getMessageManager().send(sender, "cmdEditOtherSuccess", "invite-only", kingdom.getName(), kingdom.isInviteOnly() + "");
    }
}
