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

import com.guflan.kingdomcraft.api.domain.User;
import com.guflan.kingdomcraft.api.entity.PlatformSender;
import com.guflan.kingdomcraft.api.entity.PlatformPlayer;
import com.guflan.kingdomcraft.common.AbstractKingdomCraft;
import com.guflan.kingdomcraft.common.command.CommandBaseImpl;

import java.util.concurrent.ExecutionException;

public class InviteCommand extends CommandBaseImpl {

    public InviteCommand(AbstractKingdomCraft kdc) {
        super(kdc, "invite", 1, true);
    }

    @Override
    public void execute(PlatformSender sender, String[] args) {
        if ( !sender.hasPermission("kingdom.invite") ) {
            kdc.getMessageManager().send(sender, "noPermissionCmd");
            return;
        }

        User user = kdc.getUser((PlatformPlayer) sender);

        if (user.getKingdom() == null) {
            kdc.getMessageManager().send(sender, "cmdDefaultSenderNoKingdom");
            return;
        }

        kdc.getPlugin().getScheduler().executeAsync(() -> {
            try {
                User target = kdc.getUser(args[0]).get();
                if (target == null) {
                    kdc.getMessageManager().send(sender, "cmdDefaultNoPlayer");
                    return;
                }

                if (target.getKingdom() == user.getKingdom()) {
                    kdc.getMessageManager().send(sender, "cmdInviteAlready", target.getName());
                    return;
                }

                if (target.hasInvite(user.getKingdom())) {
                    kdc.getMessageManager().send(sender, "cmdInviteAlready", target.getName());
                    return;
                }

                target.addInvite(user);
                kdc.save(target);

                PlatformPlayer targetPlayer = kdc.getPlayer(target);
                if (targetPlayer != null ) {
                    kdc.getMessageManager().send(targetPlayer, "cmdInviteTarget", user.getKingdom().getName());
                }

                kdc.getMessageManager().send(sender, "cmdInviteSender", target.getName(), user.getKingdom().getName());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
    }
}
