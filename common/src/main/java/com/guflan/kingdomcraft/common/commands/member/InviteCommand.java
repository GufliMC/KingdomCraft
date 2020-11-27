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

package com.guflan.kingdomcraft.common.commands.member;

import com.guflan.kingdomcraft.api.domain.Kingdom;
import com.guflan.kingdomcraft.api.domain.KingdomInvite;
import com.guflan.kingdomcraft.api.domain.User;
import com.guflan.kingdomcraft.api.entity.PlatformSender;
import com.guflan.kingdomcraft.api.entity.PlatformPlayer;
import com.guflan.kingdomcraft.common.KingdomCraftImpl;
import com.guflan.kingdomcraft.common.command.CommandBase;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class InviteCommand extends CommandBase {

    public InviteCommand(KingdomCraftImpl kdc) {
        super(kdc, "invite", 1, true);
        setArgumentsHint("<player>");
        setExplanationMessage("cmdInviteExplanation");
        setPermissions("kingdom.invite");
    }

    @Override
    public List<String> autocomplete(PlatformPlayer player, String[] args) {
        if ( args.length == 1 ) {
            User user = kdc.getUser(player);
            Kingdom kingdom = user.getKingdom();
            if ( kingdom != null ) {
                return kdc.getOnlinePlayers().stream().filter(p -> p != player).filter(p -> {
                    User pu = kdc.getUser(p);
                    return pu.getKingdom() == null || pu.getKingdom() != kingdom;
                }).map(PlatformPlayer::getName).collect(Collectors.toList());
            }
        }
        return null;
    }

    @Override
    public void execute(PlatformSender sender, String[] args) {
        User user = kdc.getUser((PlatformPlayer) sender);
        if (user.getKingdom() == null) {
            kdc.getMessageManager().send(sender, "cmdErrorSenderNoKingdom");
            return;
        }

        kdc.getPlugin().getScheduler().executeAsync(() -> {
            try {
                User target = kdc.getUser(args[0]).get();
                if (target == null) {
                    kdc.getMessageManager().send(sender, "cmdErrorPlayerNotExist", args[0]);
                    return;
                }

                if (target.getKingdom() == user.getKingdom()) {
                    kdc.getMessageManager().send(sender, "cmdInviteAlreadyKingdom", target.getName());
                    return;
                }

                KingdomInvite invite = target.getInvite(user.getKingdom());
                if ( invite != null && invite.isValid() && invite.getSender().equals(user) ) {
                    kdc.getMessageManager().send(sender, "cmdInviteAlready", target.getName());
                    return;
                }

                if ( invite != null ) {
                    kdc.deleteAsync(invite);
                }

                invite = target.addInvite(user);
                kdc.saveAsync(invite);

                PlatformPlayer targetPlayer = kdc.getPlayer(target);
                if (targetPlayer != null ) {
                    kdc.getMessageManager().send(targetPlayer, "cmdInviteTarget", user.getName(),
                            user.getKingdom().getName());
                }

                kdc.getMessageManager().send(sender, "cmdInviteSender", target.getName(), user.getKingdom().getName());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
    }
}
