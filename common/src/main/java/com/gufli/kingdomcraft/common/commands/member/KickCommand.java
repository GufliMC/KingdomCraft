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

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class KickCommand extends CommandBase {

    public KickCommand(KingdomCraftImpl kdc) {
        super(kdc, "kick", 1);
        setArgumentsHint("<player>");
        setExplanationMessage(() -> kdc.getMessages().getMessage("cmdKickExplanation"));
        setPermissions("kingdom.kick", "kingdom.kick.other");
    }

    @Override
    public List<String> autocomplete(PlatformPlayer player, String[] args) {
        if ( args.length == 1 ) {
            if (player.hasPermission("kingdom.kick.other")) {
                return kdc.getOnlinePlayers().stream().filter(p -> p != player).map(PlatformPlayer::getName).collect(Collectors.toList());
            }

            User user = kdc.getUser(player);
            if (player.hasPermission("kingdom.kick") && user.getKingdom() != null) {
                return kdc.getOnlinePlayers().stream().filter(p -> kdc.getUser(p).getKingdom() == user.getKingdom())
                        .map(PlatformPlayer::getName).collect(Collectors.toList());
            }
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
                if (kingdom == null) {
                    kdc.getMessages().send(sender, "cmdErrorTargetNoKingdom", target.getName());
                    return;
                }

                if ( sender instanceof PlatformPlayer) {
                    User user = kdc.getUser((PlatformPlayer) sender);
                    if ( !sender.hasPermission("kingdom.kick.other") ) {
                        if (kingdom != user.getKingdom() ) {
                            kdc.getMessages().send(sender, "cmdErrorNoPermission");
                            return;
                        }
                        if (user.getRank() == null || user.getRank().getLevel() <= target.getRank().getLevel()) {
                            kdc.getMessages().send(sender, "cmdKickLowLevel", target.getName());
                            return;
                        }
                    }
                }

                target.setKingdom(null);
                kdc.saveAsync(target);

                PlatformPlayer targetPlayer = kdc.getPlayer(target);
                if ( targetPlayer != null ) {
                    kdc.getMessages().send(targetPlayer, "cmdKickTarget", kingdom.getName());
                }

                kdc.getMessages().send(sender, "cmdKickSender", target.getName(), kingdom.getName());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
    }
}
