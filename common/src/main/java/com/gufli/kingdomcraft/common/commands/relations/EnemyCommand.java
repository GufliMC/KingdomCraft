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

public class EnemyCommand extends CommandBase {

    public EnemyCommand(KingdomCraftImpl kdc) {
        super(kdc, "enemy", 1, true);
        setArgumentsHint("<kingdom>");
        setExplanationMessage("cmdEnemyExplanation");
        setPermissions("kingdom.enemy");
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
            kdc.getMessageManager().send(sender, "cmdErrorSenderNoKingdom");
            return;
        }

        Kingdom target = kdc.getKingdom(args[0]);
        if ( target == null ) {
            kdc.getMessageManager().send(sender, "cmdErrorKingdomNotExist", args[0]);
            return;
        }

        if ( target == kingdom ) {
            kdc.getMessageManager().send(sender, "cmdErrorSameKingdom");
            return;
        }

        Relation existing = kdc.getRelation(kingdom, target);
        if ( existing != null && existing.getType() == RelationType.ENEMY ) {
            kdc.getMessageManager().send(sender, "cmdEnemyAlready", target.getName());
            return;
        }

        kdc.setRelation(kingdom, target, RelationType.ENEMY);
        kdc.getMessageManager().send(sender, "cmdEnemy", target.getName());

        for ( PlatformPlayer member : kdc.getOnlinePlayers() ) {
            Kingdom kd = kdc.getUser(member).getKingdom();
            if ( kd == kingdom && !member.equals(sender) ) {
                kdc.getMessageManager().send(member, "cmdEnemyMembers", target.getName());
            } else if ( kd == target ) {
                kdc.getMessageManager().send(member, "cmdEnemyMembers", kingdom.getName());
            }
        }
    }
}
