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
import com.gufli.kingdomcraft.api.entity.PlatformPlayer;
import com.gufli.kingdomcraft.api.entity.PlatformSender;
import com.gufli.kingdomcraft.common.KingdomCraftImpl;
import com.gufli.kingdomcraft.common.command.CommandBase;

import java.util.List;
import java.util.stream.Collectors;

public class NeutralOtherCommand extends CommandBase {

    public NeutralOtherCommand(KingdomCraftImpl kdc) {
        super(kdc, "neutral", 2);
        setArgumentsHint("<kingdom1> <kingdom2>");
        setExplanationMessage(() -> kdc.getMessages().getMessage("cmdNeutralOtherExplanation"));
        setPermissions("kingdom.neutral.other");
    }

    @Override
    public List<String> autocomplete(PlatformPlayer player, String[] args) {
        if ( args.length == 1 ) {
            return kdc.getKingdoms().stream().map(Kingdom::getName)
                    .collect(Collectors.toList());
        }
        if ( args.length == 2 ) {
            Kingdom kingdom = kdc.getKingdom(args[0]);
            return kdc.getKingdoms().stream().filter(k -> k != kingdom)
                    .map(Kingdom::getName).collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public void execute(PlatformSender sender, String[] args) {
        Kingdom kingdom = kdc.getKingdom(args[0]);
        if ( kingdom == null ) {
            kdc.getMessages().send(sender, "cmdErrorKingdomNotExist", args[0]);
            return;
        }

        Kingdom target = kdc.getKingdom(args[1]);
        if ( target == null ) {
            kdc.getMessages().send(sender, "cmdErrorKingdomNotExist", args[1]);
            return;
        }

        if ( target == kingdom ) {
            kdc.getMessages().send(sender, "cmdErrorSameKingdom");
            return;
        }

        Relation existing = kdc.getRelation(kingdom, target);
        if ( existing == null || existing.getType() == RelationType.NEUTRAL ) {
            kdc.getMessages().send(sender, "cmdNeutralOtherAlready", kingdom.getName(), target.getName());
            return;
        }

        kdc.setRelation(target, kingdom, RelationType.NEUTRAL);
        kdc.getMessages().send(sender, "cmdNeutralOther", kingdom.getName(), target.getName());

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
