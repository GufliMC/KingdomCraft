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
import com.guflan.kingdomcraft.common.command.CommandBase;

import java.util.Arrays;
import java.util.List;

public class EditInviteOnlyCommand extends CommandBase {

    public EditInviteOnlyCommand(KingdomCraftImpl kdc) {
        super(kdc, "edit invite-only", 1, true);
        setArgumentsHint("[true/false]");
        setExplanationMessage("cmdEditInviteOnlyExplanation");
        setPermissions("kingdom.edit.invite-only");
    }

    @Override
    public List<String> autocomplete(PlatformPlayer player, String[] args) {
        if ( args.length == 1 ) {
            return Arrays.asList("true", "false");
        }
        return null;
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
        kdc.saveAsync(kingdom);

        kdc.getMessageManager().send(sender, "cmdEdit", "invite-only", kingdom.isInviteOnly() + "");
    }
}
