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

package com.guflan.kingdomcraft.common.commands;

import com.guflan.kingdomcraft.api.KingdomCraftHandler;
import com.guflan.kingdomcraft.api.domain.models.Kingdom;
import com.guflan.kingdomcraft.api.domain.models.User;
import com.guflan.kingdomcraft.api.entity.CommandSender;
import com.guflan.kingdomcraft.api.entity.Player;
import com.guflan.kingdomcraft.common.command.DefaultCommandBase;

import java.util.List;
import java.util.stream.Collectors;

public class JoinCommand extends DefaultCommandBase {

    public JoinCommand(KingdomCraftHandler plugin) {
        super(plugin, "join", 1, true);
    }

    @Override
    public List<String> autocomplete(CommandSender sender, String[] args) {
        if ( !sender.hasPermission("kingdom.join") ) {
            return null;
        }
        return kdc.getKingdoms().stream().map(Kingdom::getName).collect(Collectors.toList());
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if ( !sender.hasPermission("kingdom.join") ) {
            kdc.getMessageManager().send(sender, "noPermissionCmd");
            return;
        }

        Kingdom kingdom = kdc.getKingdom(args[0]);
        if ( kingdom == null ) {
            kdc.getMessageManager().send(sender, "cmdDefaultKingdomNotExist", args[0]);
            return;
        }

        User user = kdc.getUser((Player) sender);
        if ( user.getKingdom() != null ) {
            kdc.getMessageManager().send(sender, "cmdJoinAlready");
            return;
        }

        if ( kingdom.isInviteOnly() && !user.hasInvite(kingdom) ) {
            kdc.getMessageManager().send(sender, "cmdJoinInviteOnly", kingdom.getName());
            return;
        }

        // TODO check for max members

        user.setKingdom(kingdom);
        kdc.save(user);

        kdc.getEventManager().kingdomJoin((Player) sender);

        kdc.getMessageManager().send(sender, "cmdJoinSuccess", kingdom.getName());

        for ( Player p : kdc.getOnlinePlayers() ) {
            if ( p.equals(sender) || kdc.getUser(p).getKingdom() != kingdom ) continue;
            kdc.getMessageManager().send(p, "cmdJoinSuccessMembers", user.getName());
        }
    }
}
