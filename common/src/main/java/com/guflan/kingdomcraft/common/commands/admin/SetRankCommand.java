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
import com.guflan.kingdomcraft.api.domain.Rank;
import com.guflan.kingdomcraft.api.domain.User;
import com.guflan.kingdomcraft.api.entity.PlatformSender;
import com.guflan.kingdomcraft.api.entity.PlatformPlayer;
import com.guflan.kingdomcraft.common.KingdomCraftImpl;
import com.guflan.kingdomcraft.common.command.CommandBaseImpl;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class SetRankCommand extends CommandBaseImpl {

    public SetRankCommand(KingdomCraftImpl kdc) {
        super(kdc, "setrank", 2);
    }

    @Override
    public List<String> autocomplete(PlatformSender sender, String[] args) {

        // first argument (players)
        if ( args.length == 1 ) {
            if ( sender.hasPermission("kingdom.setrank.other") ) {
                return kdc.getOnlinePlayers().stream().filter(p -> kdc.getUser(p).getKingdom() != null)
                        .map(PlatformPlayer::getName).collect(Collectors.toList());
            }

            User user = kdc.getUser((PlatformPlayer) sender);
            if ( sender.hasPermission("kingdom.setrank") && user.getKingdom() != null ) {
                return kdc.getOnlinePlayers().stream().filter(p -> kdc.getUser(p).getKingdom() == user.getKingdom())
                        .map(PlatformPlayer::getName).collect(Collectors.toList()); // TODO hierarchy check
            }
            return null;
        }

        // second argument (users)
        User user = kdc.getOnlineUser(args[0]);
        if ( user == null || user.getKingdom() == null ) {
            return null;
        }
        return user.getKingdom().getRanks().stream().map(Rank::getName).collect(Collectors.toList());
    }

    @Override
    public void execute(PlatformSender sender, String[] args) {
        if ( !sender.hasPermission("kingdom.setrank") && !sender.hasPermission("kingdom.setrank.other") ) {
            kdc.getMessageManager().send(sender, "noPermission");
            return;
        }

        kdc.getPlugin().getScheduler().executeAsync(() -> {
            try {
                User target = kdc.getUser(args[0]).get();
                if (target == null) {
                    kdc.getMessageManager().send(sender, "cmdDefaultNoPlayer", args[0]);
                    return;
                }

                if (target.getKingdom() == null) {
                    kdc.getMessageManager().send(sender, "cmdDefaultTargetNoKingdom", target.getName());
                    return;
                }

                Kingdom kingdom = target.getKingdom();
                if ( !sender.hasPermission("kingdom.setrank.other") && sender instanceof PlatformPlayer && kdc.getUser((PlatformPlayer) sender).getKingdom() != kingdom) {
                    kdc.getMessageManager().send(sender, "noPermission");
                    return;
                }

                Rank rank = kingdom.getRanks().stream().filter(r -> r.getName().equalsIgnoreCase(args[1])).findFirst().orElse(null);
                if (rank == null) {
                    kdc.getMessageManager().send(sender, "cmdSetRankNotExist", args[1]);
                    return;
                }

                if ( target.getRank() == rank ) {
                    kdc.getMessageManager().send(sender, "cmdSetRankAlready", target.getName(), rank.getName());
                    return;
                }

                // TODO hierarchy check

                Rank oldRank = target.getRank();
                target.setRank(rank);

                // async saving
                kdc.getPlugin().getScheduler().executeAsync(target::save);

                PlatformPlayer targetPlayer = kdc.getPlayer(target);
                if ( targetPlayer != null ) {
                    kdc.getEventDispatcher().dispatchRankChange(targetPlayer, oldRank);
                    kdc.getMessageManager().send(targetPlayer, "cmdSetRankTargetChange", rank.getName());
                }

                kdc.getMessageManager().send(sender, "cmdSetRankSenderChange", target.getName(), rank.getName());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
    }
}
