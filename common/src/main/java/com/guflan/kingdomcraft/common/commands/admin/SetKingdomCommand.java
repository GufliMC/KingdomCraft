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

import com.guflan.kingdomcraft.api.KingdomCraft;
import com.guflan.kingdomcraft.api.domain.models.Kingdom;
import com.guflan.kingdomcraft.api.domain.models.User;
import com.guflan.kingdomcraft.api.entity.CommandSender;
import com.guflan.kingdomcraft.api.entity.Player;
import com.guflan.kingdomcraft.common.command.DefaultCommandBase;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class SetKingdomCommand extends DefaultCommandBase {

    public SetKingdomCommand(KingdomCraft kdc) {
        super(kdc, "setkingdom", 2);
    }

    @Override
    public List<String> autocomplete(CommandSender sender, String[] args) {
        if ( !sender.hasPermission("kingdom.setkingdom") ) {
            return null;
        }

        // first argument (users)
        if ( args.length == 1 ) {
            return kdc.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
        }

        // second argument (kingdoms)
        return kdc.getKingdoms().stream().map(Kingdom::getName).collect(Collectors.toList());
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if ( !sender.hasPermission("kingdom.setkingdom") ) {
            kdc.getMessageManager().send(sender, "noPermission");
        }

        kdc.getPlugin().getScheduler().executeAsync(() -> {
            try {
                User target = kdc.getUser(args[0]).get();
                if ( target == null ) {
                    kdc.getMessageManager().send(sender, "cmdDefaultNoPlayer", args[0]);
                    return;
                }

                Kingdom kingdom = kdc.getKingdom(args[1]);
                if ( kingdom == null ) {
                    kdc.getMessageManager().send(sender, "cmdDefaultKingdomNotExist", args[0]);
                    return;
                }

                target.setKingdom(kingdom);
                kdc.save(target);

                Player targetPlayer = kdc.getPlayer(target);
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
