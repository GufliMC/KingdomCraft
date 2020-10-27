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
import com.guflan.kingdomcraft.api.domain.User;
import com.guflan.kingdomcraft.api.entity.PlatformSender;
import com.guflan.kingdomcraft.api.entity.PlatformPlayer;
import com.guflan.kingdomcraft.common.KingdomCraftImpl;
import com.guflan.kingdomcraft.common.command.CommandBaseImpl;

public class EditInviteOnlyCommand extends CommandBaseImpl {

    public EditInviteOnlyCommand(KingdomCraftImpl kdc) {
        super(kdc, "edit invite-only", 1, true);
        setArgumentsHint("[true/false]");
        setExplanationMessage(kdc.getMessageManager().getMessage("cmdEditInviteOnlyExplanation"));
        setPermissions("kingdom.edit.invite-only");
    }

    @Override
    public void execute(PlatformSender sender, String[] args) {
        User user = kdc.getUser((PlatformPlayer) sender);
        Kingdom kingdom = user.getKingdom();
        if ( kingdom == null ) {
            kdc.getMessageManager().send(sender, "cmdErrorSenderNoKingdom");
            return;
        }

        if ( !args[0].equalsIgnoreCase("true")
                && !args[0].equalsIgnoreCase("false") ) {
            kdc.getMessageManager().send(sender, "errorInvalidBoolean", args[0]);
            return;
        }

        kingdom.setInviteOnly(Boolean.parseBoolean(args[0]));

        // async saving
        kdc.getPlugin().getScheduler().executeAsync(kingdom::save);

        kdc.getMessageManager().send(sender, "cmdEditSuccess", "invite-only", kingdom.isInviteOnly() + "");
    }
}
