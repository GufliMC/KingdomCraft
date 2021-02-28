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

package com.gufli.kingdomcraft.common.commands.admin;

import com.gufli.kingdomcraft.api.entity.PlatformPlayer;
import com.gufli.kingdomcraft.api.entity.PlatformSender;
import com.gufli.kingdomcraft.common.KingdomCraftImpl;
import com.gufli.kingdomcraft.common.command.CommandBase;

public class PlayerDataPurgeCommand extends CommandBase {

    public PlayerDataPurgeCommand(KingdomCraftImpl kdc) {
        super(kdc, "playerdata purge", 0, true);
        setExplanationMessage(() -> kdc.getMessages().getMessage("cmdPlayerDataPurgeExplanation"));
        setPermissions("kingdom.playerdata.purge");
    }

    @Override
    public void execute(PlatformSender sender, String[] args) {
        if ( sender instanceof PlatformPlayer ) {
            sender.sendMessage("For security reasons, this command can only be executed in the console!");
            return;
        }

        kdc.purgeUsers();
        sender.sendMessage("Deleted players that have been inactive for 14 days or more!");
    }
}
