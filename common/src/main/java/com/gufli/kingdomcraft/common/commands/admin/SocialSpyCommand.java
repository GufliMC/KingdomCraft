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

public class SocialSpyCommand extends CommandBase {

    public SocialSpyCommand(KingdomCraftImpl kdc) {
        super(kdc, "socialspy", 0, true);
        addCommand("ss");
        setExplanationMessage(() -> kdc.getMessages().getMessage("cmdSocialSpyExplanation"));
        setPermissions("kingdom.socialspy");
    }

    @Override
    public void execute(PlatformSender sender, String[] args) {
        PlatformPlayer player = (PlatformPlayer) sender;

        if ( player.isSocialSpyEnabled() ) {
            player.setSocialSpyEnabled(false);
            kdc.saveAsync(player.getUser());
            kdc.getMessages().send(sender, "cmdSocialSpyDisable");
        } else {
            player.setSocialSpyEnabled(true);
            kdc.saveAsync(player.getUser());
            kdc.getMessages().send(sender, "cmdSocialSpyEnable");
        }
    }
}
