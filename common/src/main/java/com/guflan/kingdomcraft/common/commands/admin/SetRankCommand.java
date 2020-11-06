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
import com.guflan.kingdomcraft.common.command.CommandBase;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class SetRankCommand extends CommandBase {

    public SetRankCommand(KingdomCraftImpl kdc) {
        super(kdc, "setrank", 2);
        setArgumentsHint("<player> <rank>");
        setExplanationMessage("cmdSetRankExplanation");
        setPermissions("kingdom.setrank", "kingdom.setrank.other");
    }

    @Override
    public List<String> autocomplete(PlatformPlayer player, String[] args) {
        if ( args.length == 1 ) {
            if ( player.hasPermission("kingdom.setrank.other") ) {
                return kdc.getOnlinePlayers().stream().filter(p -> kdc.getUser(p).getKingdom() != null)
                        .map(PlatformPlayer::getName).collect(Collectors.toList());
            }

            User user = kdc.getUser(player);
            if ( player.hasPermission("kingdom.setrank") && user.getKingdom() != null ) {
                return kdc.getOnlinePlayers().stream().filter(p -> kdc.getUser(p).getKingdom() == user.getKingdom())
                        .filter(p -> {
                            User u = kdc.getUser(p);
                            return u.getRank().getLevel() < user.getRank().getLevel();
                        })
                        .map(PlatformPlayer::getName).collect(Collectors.toList());
            }
            return null;
        }
        if ( args.length == 2 ) {
            User user = kdc.getUser(player);
            if ( user.getKingdom() == null ) {
                return null;
            }

            User target = kdc.getOnlineUser(args[0]);
            if (target == null || target.getKingdom() == null) {
                return null;
            }

            if ( !player.hasPermission("kingdom.setrank.other") && user.getKingdom() != target.getKingdom() ) {
                return null;
            }

            return user.getKingdom().getRanks().stream()
                    .filter(rank -> rank.getMaxMembers() == 0 || rank.getMemberCount() < rank.getMaxMembers())
                    .map(Rank::getName).collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public void execute(PlatformSender sender, String[] args) {
        kdc.getPlugin().getScheduler().executeAsync(() -> {
            try {
                User target = kdc.getUser(args[0]).get();
                if (target == null) {
                    kdc.getMessageManager().send(sender, "cmdErrorNoPlayer", args[0]);
                    return;
                }

                Kingdom kingdom = target.getKingdom();
                if ( kingdom == null ) {
                    kdc.getMessageManager().send(sender, "cmdErrorTargetNoKingdom", target.getName());
                    return;
                }

                Rank rank = kingdom.getRanks().stream().filter(r -> r.getName().equalsIgnoreCase(args[1])).findFirst().orElse(null);
                if ( rank == null ) {
                    kdc.getMessageManager().send(sender, "cmdSetRankNotExist", args[1]);
                    return;
                }

                if ( !sender.hasPermission("kingdom.setrank.other") ) {
                    User user = kdc.getUser((PlatformPlayer) sender);
                    if ( user.getKingdom() != kingdom ) {
                        kdc.getMessageManager().send(sender, "cmdErrorNoPermission");
                        return;
                    }
                    if ( user.getRank() == null || user.getRank().getLevel() <= rank.getLevel() ) {
                        kdc.getMessageManager().send(sender, "cmdSetRankLowLevelTarget");
                        return;
                    }
                    if ( target.getRank() != null && user.getRank().getLevel() <= target.getRank().getLevel() ) {
                        kdc.getMessageManager().send(sender, "cmdSetRankLowLevelCurrent", target.getName());
                        return;
                    }
                }

                if ( target.getRank() == rank ) {
                    kdc.getMessageManager().send(sender, "cmdSetRankAlready", target.getName(), rank.getName());
                    return;
                }

                if ( !sender.hasPermission("kingdom.setrank.other") && rank.getMaxMembers() > 0
                        && rank.getMaxMembers() >= rank.getMemberCount() ) {
                    kdc.getMessageManager().send(sender, "cmdSetRankFull", rank.getName());
                    return;
                }

                Rank oldRank = target.getRank();
                target.setRank(rank);
                kdc.saveAsync(target);

                PlatformPlayer targetPlayer = kdc.getPlayer(target);
                if ( targetPlayer != null ) {
                    kdc.getPlugin().getScheduler().executeSync(() ->
                            kdc.getEventDispatcher().dispatchRankChange(targetPlayer, oldRank));
                    kdc.getMessageManager().send(targetPlayer, "cmdSetRankTargetChange", rank.getName());
                }

                kdc.getMessageManager().send(sender, "cmdSetRankSenderChange", target.getName(), rank.getName());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
    }
}
