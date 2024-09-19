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
import com.gufli.kingdomcraft.api.domain.User;
import com.gufli.kingdomcraft.api.entity.PlatformSender;
import com.gufli.kingdomcraft.api.entity.PlatformPlayer;
import com.gufli.kingdomcraft.common.KingdomCraftImpl;
import com.gufli.kingdomcraft.common.command.CommandBase;

public class EditMaxMembersCommand extends CommandBase {

    public EditMaxMembersCommand(KingdomCraftImpl kdc) {
        super(kdc, "edit max-members", 1, true);
        setArgumentsHint("<amount>");
        setExplanationMessage(() -> kdc.getMessages().getMessage("cmdExitMaxMembersExplanation"));
        setPermissions("kingdom.edit.max-members");
    }

    @Override
    public void execute(PlatformSender sender, String[] args) {
        User user = kdc.getUser((PlatformPlayer) sender);
        Kingdom kingdom = user.getKingdom();
        if ( kingdom == null ) {
            kdc.getMessages().send(sender, "cmdErrorSenderNoKingdom");
            return;
        }

        if ( !args[0].matches("[0-9]+") ) {
            kdc.getMessages().send(sender, "cmdErrorInvalidNumber", args[0]);
            return;
        }

        kingdom.setMaxMembers(Integer.parseInt(args[0]));
        kdc.saveAsync(kingdom);

        kdc.getMessages().send(sender, "cmdEdit", "max-members", kingdom.getMaxMembers() + "");
    }
}
