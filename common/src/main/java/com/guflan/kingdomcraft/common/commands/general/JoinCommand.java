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

package com.guflan.kingdomcraft.common.commands.general;

import com.guflan.kingdomcraft.api.domain.Kingdom;
import com.guflan.kingdomcraft.api.domain.KingdomInvite;
import com.guflan.kingdomcraft.api.domain.User;
import com.guflan.kingdomcraft.api.entity.PlatformPlayer;
import com.guflan.kingdomcraft.api.entity.PlatformSender;
import com.guflan.kingdomcraft.common.KingdomCraftImpl;
import com.guflan.kingdomcraft.common.command.CommandBase;

import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class JoinCommand extends CommandBase {

    public JoinCommand(KingdomCraftImpl kdc) {
        super(kdc, "join", 1, true);
        setArgumentsHint("<kingdom>");
        setExplanationMessage("cmdJoinExplanation");
        setPermissions("kingdom.join");
    }

    @Override
    public List<String> autocomplete(PlatformPlayer sender, String[] args) {
        return kdc.getKingdoms().stream().map(Kingdom::getName).collect(Collectors.toList());
    }

    @Override
    public void execute(PlatformSender sender, String[] args) {
        Kingdom kingdom = kdc.getKingdom(args[0]);
        if ( kingdom == null ) {
            kdc.getMessageManager().send(sender, "cmdErrorKingdomNotExist", args[0]);
            return;
        }

        if ( kingdom.getDefaultRank() == null ) {
            kdc.getMessageManager().send(sender, "cmdErrorKingdomNoDefaultRank", kingdom.getName());
            return;
        }

        User user = kdc.getUser((PlatformPlayer) sender);
        if ( user.getKingdom() != null ) {
            kdc.getMessageManager().send(sender, "cmdJoinAlready");
            return;
        }

        if ( kingdom.isInviteOnly() ) {
            KingdomInvite invite = user.getInvite(kingdom);
            if ( invite == null || !invite.isValid() ) {
                kdc.getMessageManager().send(sender, "cmdJoinNoInvite", kingdom.getName());
                return;
            }
        }

        if ( kingdom.getMaxMembers() > 0 && kingdom.getMaxMembers() >= kingdom.getMemberCount() ) {
            kdc.getMessageManager().send(sender, "cmdJoinFull", kingdom.getName());
            return;
        }

        user.setKingdom(kingdom);

        kdc.saveAsync(user).thenRun(user::clearInvites).exceptionally(ex -> {
            kdc.getPlugin().log(ex.getMessage(), Level.SEVERE);
            return null;
        });

        kdc.getEventDispatcher().dispatchKingdomJoin((PlatformPlayer) sender);

        kdc.getMessageManager().send(sender, "cmdJoin", kingdom.getName());

        for ( PlatformPlayer p : kdc.getOnlinePlayers() ) {
            if ( p.equals(sender) || kdc.getUser(p).getKingdom() != kingdom ) continue;
            kdc.getMessageManager().send(p, "cmdJoinMembers", user.getName());
        }
    }
}
