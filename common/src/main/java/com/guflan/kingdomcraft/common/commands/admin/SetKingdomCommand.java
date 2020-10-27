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
import com.guflan.kingdomcraft.common.KingdomCraftImpl;
import com.guflan.kingdomcraft.common.command.CommandBaseImpl;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class SetKingdomCommand extends CommandBaseImpl {

    public SetKingdomCommand(KingdomCraftImpl kdc) {
        super(kdc, "setkingdom", 2);
        setArgumentsHint("<player> <kingdom>");
        setExplanationMessage(kdc.getMessageManager().getMessage("cmdSetKingdomExplanation"));
        setPermissions("kingdom.setkingdom");
    }

    @Override
    public List<String> autocomplete(PlatformSender sender, String[] args) {
        // first argument (users)
        if ( args.length == 1 ) {
            return kdc.getOnlinePlayers().stream().map(PlatformPlayer::getName).collect(Collectors.toList());
        }

        // second argument (kingdoms)
        return kdc.getKingdoms().stream().map(Kingdom::getName).collect(Collectors.toList());
    }

    @Override
    public void execute(PlatformSender sender, String[] args) {
        kdc.getPlugin().getScheduler().executeAsync(() -> {
            try {
                User target = kdc.getUser(args[0]).get();
                if ( target == null ) {
                    kdc.getMessageManager().send(sender, "cmdErrorNoPlayer", args[0]);
                    return;
                }

                Kingdom kingdom = kdc.getKingdom(args[1]);
                if ( kingdom == null ) {
                    kdc.getMessageManager().send(sender, "cmdErrorKingdomNotExist", args[0]);
                    return;
                }

                if ( kingdom.getDefaultRank() == null ) {
                    kdc.getMessageManager().send(sender, "cmdErrorKingdomNoDefaultRank", kingdom.getName());
                    return;
                }

                if ( target.getKingdom() == kingdom ) {
                    kdc.getMessageManager().send(sender, "cmdSetKingdomAlready", target.getName(), kingdom.getName());
                    return;
                }

                Kingdom oldKingdom = target.getKingdom();
                target.setKingdom(kingdom);

                if ( kingdom.getDefaultRank() != null ) {
                    target.setRank(kingdom.getDefaultRank());
                }

                // async saving
                kdc.getPlugin().getScheduler().executeAsync(target::save);

                PlatformPlayer tplayer = kdc.getPlayer(target);
                if ( tplayer != null ) {
                    if ( oldKingdom != null ) {
                        kdc.getEventDispatcher().dispatchKingdomLeave(tplayer, oldKingdom);
                    }
                    kdc.getEventDispatcher().dispatchKingdomJoin(tplayer);
                }

                PlatformPlayer targetPlayer = kdc.getPlayer(target);
                if ( targetPlayer != null ) {
                    kdc.getMessageManager().send(targetPlayer, "cmdSetKingdomTarget", kingdom.getName());
                }

                kdc.getMessageManager().send(sender, "cmdSetKingdomSender", target.getName(), kingdom.getName());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
    }
}
