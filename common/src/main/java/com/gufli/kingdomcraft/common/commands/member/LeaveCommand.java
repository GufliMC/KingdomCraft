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

package com.gufli.kingdomcraft.common.commands.member;

import com.gufli.kingdomcraft.api.domain.Kingdom;
import com.gufli.kingdomcraft.api.domain.User;
import com.gufli.kingdomcraft.api.entity.PlatformSender;
import com.gufli.kingdomcraft.api.entity.PlatformPlayer;
import com.gufli.kingdomcraft.common.KingdomCraftImpl;
import com.gufli.kingdomcraft.common.command.CommandBase;

public class LeaveCommand extends CommandBase {

    public LeaveCommand(KingdomCraftImpl kdc) {
        super(kdc, "leave", 0, true);
        setExplanationMessage("cmdLeaveExplanation");
        setPermissions("kingdom.leave");
    }

    @Override
    public void execute(PlatformSender sender, String[] args) {
        User user = kdc.getUser((PlatformPlayer) sender);
        if (user.getKingdom() == null) {
            kdc.getMessageManager().send(sender, "cmdErrorSenderNoKingdom");
            return;
        }

        Kingdom oldKingdom = user.getKingdom();
        user.setKingdom(null);
        kdc.saveAsync(user);

        kdc.getMessageManager().send(sender, "cmdLeave", oldKingdom.getName());

        for ( PlatformPlayer member : kdc.getOnlinePlayers() ) {
            if ( kdc.getUser(member).getKingdom() != oldKingdom ) continue;
            kdc.getMessageManager().send(member, "cmdLeaveMembers", user.getName());
        }
    }
}
