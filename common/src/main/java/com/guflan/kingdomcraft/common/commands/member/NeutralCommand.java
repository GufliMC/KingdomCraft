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

package com.guflan.kingdomcraft.common.commands.member;

import com.guflan.kingdomcraft.api.domain.Kingdom;
import com.guflan.kingdomcraft.api.domain.Relation;
import com.guflan.kingdomcraft.api.domain.RelationType;
import com.guflan.kingdomcraft.api.domain.User;
import com.guflan.kingdomcraft.api.entity.PlatformSender;
import com.guflan.kingdomcraft.api.entity.PlatformPlayer;
import com.guflan.kingdomcraft.common.KingdomCraftImpl;
import com.guflan.kingdomcraft.common.command.CommandBaseImpl;

public class NeutralCommand extends CommandBaseImpl {

    public NeutralCommand(KingdomCraftImpl kdc) {
        super(kdc, "neutral", 1, true);
        setArgumentsHint("<kingdom>");
        setExplanationMessage(kdc.getMessageManager().getMessage("cmdNeutralExplanation"));
        setPermissions("kingdom.neutral");
    }

    @Override
    public void execute(PlatformSender sender, String[] args) {
        User user = kdc.getUser((PlatformPlayer) sender);
        Kingdom kingdom = user.getKingdom();
        if ( kingdom == null ) {
            kdc.getMessageManager().send(sender, "cmdErrorSenderNoKingdom");
            return;
        }

        Kingdom target = kdc.getKingdom(args[0]);
        if ( target == null ) {
            kdc.getMessageManager().send(sender, "cmdErrorKingdomNotExist", args[0]);
            return;
        }

        Relation existing = kdc.getRelation(kingdom, target);
        if ( existing == null || existing.getType() == RelationType.NEUTRAL ) {
            kdc.getMessageManager().send(sender, "cmdNeutralAlready", target.getName());
            return;
        }

        // if they are enemies or in truce, it must be requested
        if ( (existing.getType() == RelationType.ENEMY || existing.getType() == RelationType.TRUCE) ) {

            Relation request = kdc.getRelationRequest(target, kingdom);
            if ( request == null || request.getType() != RelationType.NEUTRAL ) {

                request = kdc.getRelationRequest(kingdom, target);
                if ( request != null && request.getType() == RelationType.NEUTRAL ) {
                    kdc.getMessageManager().send(sender, "cmdNeutralRequestAlready", target.getName());
                    return;
                }

                kdc.addRelationRequest(kingdom, target, RelationType.NEUTRAL);
                kdc.getMessageManager().send(sender, "cmdNeutralRequest", target.getName());

                for (PlatformPlayer member : kdc.getOnlinePlayers()) {
                    if (kdc.getUser(member).getKingdom() != target) continue;
                    kdc.getMessageManager().send(sender, "cmdNeutralRequestTarget", kingdom.getName());
                }

                return;
            }
        }

        kdc.removeRelationRequest(target, kingdom);
        kdc.setRelation(target, kingdom, RelationType.NEUTRAL);
        kdc.getMessageManager().send(sender, "cmdNeutralAccepted", target.getName());

        for ( PlatformPlayer member : kdc.getOnlinePlayers() ) {
            if ( kdc.getUser(member).getKingdom() != target ) continue;
            kdc.getMessageManager().send(sender, "cmdNeutralAccepted", kingdom.getName());
        }
    }
}
