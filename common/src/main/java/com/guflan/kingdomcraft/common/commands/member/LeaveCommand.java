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

package com.guflan.kingdomcraft.common.commands.member;

import com.guflan.kingdomcraft.api.domain.Kingdom;
import com.guflan.kingdomcraft.api.domain.User;
import com.guflan.kingdomcraft.api.entity.PlatformSender;
import com.guflan.kingdomcraft.api.entity.PlatformPlayer;
import com.guflan.kingdomcraft.common.KingdomCraftImpl;
import com.guflan.kingdomcraft.common.command.CommandBaseImpl;

public class LeaveCommand extends CommandBaseImpl {

    public LeaveCommand(KingdomCraftImpl kdc) {
        super(kdc, "leave", 0, true);
        setExplanationMessage(kdc.getMessageManager().getMessage("cmdLeaveExplanation"));
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


        // async saving
        kdc.getPlugin().getScheduler().executeAsync(user::save);

        kdc.getEventDispatcher().dispatchKingdomLeave((PlatformPlayer) sender, oldKingdom);

        kdc.getMessageManager().send(sender, "cmdLeaveSuccess", oldKingdom.getName());

        for ( PlatformPlayer member : kdc.getOnlinePlayers() ) {
            if ( kdc.getUser(member).getKingdom() != oldKingdom ) continue;
            kdc.getMessageManager().send(member, "cmdLeaveSuccessMembers", user.getName());
        }
    }
}
