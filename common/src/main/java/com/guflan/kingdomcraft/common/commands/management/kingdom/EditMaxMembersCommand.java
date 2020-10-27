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

public class EditMaxMembersCommand extends CommandBaseImpl {

    public EditMaxMembersCommand(KingdomCraftImpl kdc) {
        super(kdc, "edit max-members", 1, true);
        setArgumentsHint("<amount>");
        setExplanationMessage(kdc.getMessageManager().getMessage("cmdExitMaxMembersExplanation"));
        setPermissions("kingdom.edit.max-members");
    }

    @Override
    public void execute(PlatformSender sender, String[] args) {
        User user = kdc.getUser((PlatformPlayer) sender);
        Kingdom kingdom = user.getKingdom();
        if ( kingdom == null ) {
            kdc.getMessageManager().send(sender, "cmdErrorSenderNoKingdom");
            return;
        }

        if ( !args[0].matches("[0-9]+") ) {
            kdc.getMessageManager().send(sender, "errorInvalidNumber", args[0]);
            return;
        }

        kingdom.setMaxMembers(Integer.parseInt(args[0]));

        // async saving
        kdc.getPlugin().getScheduler().executeAsync(kingdom::save);

        kdc.getMessageManager().send(sender, "cmdEditSuccess", "max-members", kingdom.getMaxMembers() + "");
    }
}
