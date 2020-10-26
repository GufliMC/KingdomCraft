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

package com.guflan.kingdomcraft.common.commands.admin;

import com.guflan.kingdomcraft.api.entity.PlatformPlayer;
import com.guflan.kingdomcraft.api.entity.PlatformSender;
import com.guflan.kingdomcraft.common.KingdomCraftImpl;
import com.guflan.kingdomcraft.common.command.CommandBase;

public class AdminCommand extends CommandBase {

    public AdminCommand(KingdomCraftImpl kdc) {
        super(kdc, "admin", 0, true);
        setExplanationMessage(kdc.getMessageManager().getMessage("cmdAdminExplanation"));
        setPermissions("kingdom.admin");
    }

    @Override
    public void execute(PlatformSender sender, String[] args) {
        PlatformPlayer player = (PlatformPlayer) sender;

        if ( player.isAdmin() ) {
            player.setAdmin(false);
            kdc.getMessageManager().send(sender, "cmdAdminDisable");
        } else {
            player.setAdmin(true);
            kdc.getMessageManager().send(sender, "cmdAdminEnable");
        }
    }
}
