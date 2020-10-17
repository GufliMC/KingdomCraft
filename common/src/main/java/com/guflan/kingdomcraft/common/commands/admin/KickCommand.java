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

import com.guflan.kingdomcraft.api.domain.Kingdom;
import com.guflan.kingdomcraft.api.domain.User;
import com.guflan.kingdomcraft.api.entity.PlatformSender;
import com.guflan.kingdomcraft.api.entity.PlatformPlayer;
import com.guflan.kingdomcraft.common.AbstractKingdomCraft;
import com.guflan.kingdomcraft.common.command.CommandBaseImpl;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class KickCommand extends CommandBaseImpl {

    public KickCommand(AbstractKingdomCraft kdc) {
        super(kdc, "kick", 1);
    }

    @Override
    public List<String> autocomplete(PlatformSender sender, String[] args) {
        if ( sender.hasPermission("kingdom.kick.other") ) {
            return kdc.getOnlinePlayers().stream().map(PlatformPlayer::getName).collect(Collectors.toList());
        }

        User user = kdc.getUser((PlatformPlayer) sender);
        if ( sender.hasPermission("kingdom.kick") && user.getKingdom() != null ) {
            return kdc.getOnlinePlayers().stream().filter(p -> kdc.getUser(p).getKingdom() == user.getKingdom())
                    .map(PlatformPlayer::getName).collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public void execute(PlatformSender sender, String[] args) {
        if ( !sender.hasPermission("kingdom.kick") && !sender.hasPermission("kingdom.kick.other")) {
            kdc.getMessageManager().send(sender, "noPermissionCmd");
            return;
        }

        kdc.getPlugin().getScheduler().executeAsync(() -> {
            try {
                User target = kdc.getUser(args[0]).get();
                if (target == null) {
                    kdc.getMessageManager().send(sender, "cmdDefaultNoPlayer", args[0]);
                    return;
                }

                Kingdom kingdom = target.getKingdom();
                if (kingdom == null) {
                    kdc.getMessageManager().send(sender, "cmdDefaultTargetNoKingdom", target.getName());
                    return;
                }

                if ( sender instanceof PlatformPlayer) {
                    User user = kdc.getUser((PlatformPlayer) sender);

                    // kick other kingdom
                    if (kingdom != user.getKingdom() && !sender.hasPermission("kingdom.kick.other")) {
                        kdc.getMessageManager().send(sender, "noPermissionCmd");
                        return;
                    }

                    // kick own kingdom
                    // TODO hierarchy check
                }

                target.setKingdom(null);
                kdc.save(target);

                PlatformPlayer tplayer = kdc.getPlayer(target);
                if ( tplayer != null ) {
                    kdc.getEventDispatcher().dispatchKingdomLeave(tplayer, kingdom);
                }

                PlatformPlayer targetPlayer = kdc.getPlayer(target);
                if ( targetPlayer != null ) {
                    kdc.getMessageManager().send(targetPlayer, "cmdKickTarget", kingdom.getName());
                }

                kdc.getMessageManager().send(sender, "cmdKickSender", target.getName(), kingdom.getName());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
    }
}
