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
import com.gufli.kingdomcraft.api.domain.Rank;
import com.gufli.kingdomcraft.api.domain.User;
import com.gufli.kingdomcraft.api.entity.PlatformPlayer;
import com.gufli.kingdomcraft.api.entity.PlatformSender;
import com.gufli.kingdomcraft.common.KingdomCraftImpl;
import com.gufli.kingdomcraft.common.command.CommandBase;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class PromoteCommand extends CommandBase {

    public PromoteCommand(KingdomCraftImpl kdc) {
        super(kdc, "promote", 1);
        setArgumentsHint("<player>");
        setExplanationMessage(() -> kdc.getMessages().getMessage("cmdPromoteExplanation"));
        setPermissions("kingdom.promote", "kingdom.promote.other");
    }

    @Override
    public List<String> autocomplete(PlatformPlayer player, String[] args) {
        if ( args.length == 1 ) {
            if ( player.hasPermission("kingdom.promote.other") ) {
                return kdc.getOnlinePlayers().stream().filter(p -> kdc.getUser(p).getKingdom() != null)
                        .map(PlatformPlayer::getName).collect(Collectors.toList());
            }

            User user = kdc.getUser(player);
            if ( player.hasPermission("kingdom.promote") && user.getKingdom() != null ) {
                return kdc.getOnlinePlayers().stream().filter(p -> kdc.getUser(p).getKingdom() == user.getKingdom())
                        .filter(p -> {
                            User u = kdc.getUser(p);
                            return u.getRank() == null || u.getRank().getLevel() < user.getRank().getLevel();
                        })
                        .map(PlatformPlayer::getName).collect(Collectors.toList());
            }
            return null;
        }
        return null;
    }

    @Override
    public void execute(PlatformSender sender, String[] args) {
        kdc.getPlugin().getScheduler().executeAsync(() -> {
            try {
                User target = kdc.getUser(args[0]).get();
                if (target == null) {
                    kdc.getMessages().send(sender, "cmdErrorPlayerNotExist", args[0]);
                    return;
                }

                Kingdom kingdom = target.getKingdom();
                if ( kingdom == null ) {
                    kdc.getMessages().send(sender, "cmdErrorTargetNoKingdom", target.getName());
                    return;
                }

                List<Rank> ranks = kingdom.getRanks().stream()
                        .sorted(Comparator.comparingInt(Rank::getLevel).thenComparing(Rank::getName))
                        .collect(Collectors.toList());

                int index = ranks.indexOf(target.getRank());
                Rank rank;
                if ( index == -1 ) {
                    if ( kingdom.getDefaultRank() != null ) {
                        rank = kingdom.getDefaultRank();
                    } else {
                        rank = ranks.get(0);
                    }
                } else if ( index == ranks.size() - 1 ) {
                    kdc.getMessages().send(sender, "cmdPromoteHighest");
                    return;
                } else {
                    rank = ranks.get(index + 1);
                }

                if ( !sender.hasPermission("kingdom.promote.other") ) {
                    User user = kdc.getUser((PlatformPlayer) sender);
                    if ( user.getKingdom() != kingdom ) {
                        kdc.getMessages().send(sender, "cmdErrorNoPermission");
                        return;
                    }
                    if ( user.getRank() == null || user.getRank().getLevel() <= rank.getLevel() ) {
                        kdc.getMessages().send(sender, "cmdSetRankLowLevelTarget", rank.getName());
                        return;
                    }
                    if ( target.getRank() != null && user.getRank().getLevel() <= target.getRank().getLevel() ) {
                        kdc.getMessages().send(sender, "cmdSetRankLowLevelCurrent", target.getName());
                        return;
                    }
                }

                if ( !sender.hasPermission("kingdom.promote.other") && rank.getMaxMembers() > 0
                        && rank.getMaxMembers() <= rank.getMemberCount() ) {
                    kdc.getMessages().send(sender, "cmdSetRankFull", rank.getName());
                    return;
                }

                target.setRank(rank);
                kdc.saveAsync(target);

                PlatformPlayer targetPlayer = kdc.getPlayer(target);
                if ( targetPlayer != null ) {
                    kdc.getMessages().send(targetPlayer, "cmdSetRankTarget", rank.getName());
                }

                kdc.getMessages().send(sender, "cmdSetRankSender", target.getName(), rank.getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
