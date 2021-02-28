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

package com.gufli.kingdomcraft.common.commands.relations;

import com.gufli.kingdomcraft.api.domain.Kingdom;
import com.gufli.kingdomcraft.api.domain.Relation;
import com.gufli.kingdomcraft.api.domain.RelationType;
import com.gufli.kingdomcraft.api.domain.User;
import com.gufli.kingdomcraft.api.entity.PlatformSender;
import com.gufli.kingdomcraft.api.entity.PlatformPlayer;
import com.gufli.kingdomcraft.common.KingdomCraftImpl;
import com.gufli.kingdomcraft.common.command.CommandBase;

import java.util.List;
import java.util.stream.Collectors;

public class NeutralCommand extends CommandBase {

    public NeutralCommand(KingdomCraftImpl kdc) {
        super(kdc, "neutral", 1, true);
        setArgumentsHint("<kingdom>");
        setExplanationMessage("cmdNeutralExplanation");
        setPermissions("kingdom.neutral");
    }

    @Override
    public List<String> autocomplete(PlatformPlayer player, String[] args) {
        if ( args.length == 1 ) {
            User user = kdc.getUser(player);
            Kingdom kingdom = user.getKingdom();
            if ( kingdom != null ) {
                return kdc.getKingdoms().stream().filter(k -> k != kingdom)
                        .map(Kingdom::getName).collect(Collectors.toList());
            }
        }
        return null;
    }

    @Override
    public void execute(PlatformSender sender, String[] args) {
        User user = kdc.getUser((PlatformPlayer) sender);
        Kingdom kingdom = user.getKingdom();
        if ( kingdom == null ) {
            kdc.getMessages().send(sender, "cmdErrorSenderNoKingdom");
            return;
        }

        Kingdom target = kdc.getKingdom(args[0]);
        if ( target == null ) {
            kdc.getMessages().send(sender, "cmdErrorKingdomNotExist", args[0]);
            return;
        }

        if ( target == kingdom ) {
            kdc.getMessages().send(sender, "cmdErrorSameKingdom");
            return;
        }

        Relation existing = kdc.getRelation(kingdom, target);
        if ( existing == null || existing.getType() == RelationType.NEUTRAL ) {
            kdc.getMessages().send(sender, "cmdNeutralAlready", target.getName());
            return;
        }

        // if they are enemies or in truce, it must be requested
        if ( (existing.getType() == RelationType.ENEMY || existing.getType() == RelationType.TRUCE) ) {

            Relation request = kdc.getRelationRequest(target, kingdom);
            if ( request == null || request.getType() != RelationType.NEUTRAL ) {

                request = kdc.getRelationRequest(kingdom, target);
                if ( request != null && request.getType() == RelationType.NEUTRAL ) {
                    kdc.getMessages().send(sender, "cmdNeutralRequestAlready", target.getName());
                    return;
                }

                kdc.addRelationRequest(kingdom, target, RelationType.NEUTRAL);
                kdc.getMessages().send(sender, "cmdNeutralRequest", target.getName());

                for (PlatformPlayer member : kdc.getOnlinePlayers()) {
                    if ( kdc.getUser(member).getKingdom() != target || !member.hasPermission("kingdom.neutral") ) continue;
                    kdc.getMessages().send(member, "cmdNeutralRequestTarget", kingdom.getName());
                }

                return;
            }
        }

        kdc.setRelation(target, kingdom, RelationType.NEUTRAL);

        for ( PlatformPlayer member : kdc.getOnlinePlayers() ) {
            Kingdom kd = member.getUser().getKingdom();
            if ( kd == kingdom ) {
                kdc.getMessages().send(member, "cmdNeutral", target.getName());
            } else if ( kd == target ) {
                kdc.getMessages().send(member, "cmdNeutral", kingdom.getName());
            }
        }
    }
}
