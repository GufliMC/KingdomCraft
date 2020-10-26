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
import com.guflan.kingdomcraft.common.command.CommandBase;

public class EditPrefixOtherCommand extends CommandBase {

    public EditPrefixOtherCommand(KingdomCraftImpl kdc) {
        super(kdc, "edit prefix", 2);
        setArgumentsHint("<kingdom> <prefix>");
        setExplanationMessage(kdc.getMessageManager().getMessage("cmdExitPrefixOtherExplanation"));
        setPermissions("kingdom.edit.prefix.other");
    }

    @Override
    public void execute(PlatformSender sender, String[] args) {
        Kingdom kingdom = kdc.getKingdom(args[0]);
        if ( kingdom == null ) {
            kdc.getMessageManager().send(sender, "cmdErrorKingdomNotExist", args[0]);
            return;
        }

        kingdom.setPrefix(args[1]);

        // async saving
        kdc.getPlugin().getScheduler().executeAsync(kingdom::save);

        kdc.getMessageManager().send(sender, "cmdEditOtherSuccess", "prefix", kingdom.getName(), args[1]);
    }
}
