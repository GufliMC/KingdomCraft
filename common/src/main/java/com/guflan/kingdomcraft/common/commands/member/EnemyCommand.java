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
import com.guflan.kingdomcraft.common.command.CommandBase;

public class EnemyCommand extends CommandBase {

    public EnemyCommand(KingdomCraftImpl kdc) {
        super(kdc, "enemy", 1, true);
        setArgumentsHint("<kingdom>");
        setExplanationMessage(kdc.getMessageManager().getMessage("cmdEnemyExplanation"));
        setPermissions("kingdom.enemy");
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
        if ( existing != null && existing.getType() == RelationType.ENEMY ) {
            kdc.getMessageManager().send(sender, "cmdEnemyAlready", target.getName());
            return;
        }

        kdc.setRelation(kingdom, target, RelationType.ENEMY);
        kdc.getMessageManager().send(sender, "cmdEnemySuccess", target.getName());

        for ( PlatformPlayer member : kdc.getOnlinePlayers() ) {
            Kingdom kd = kdc.getUser(member).getKingdom();
            if ( kd == kingdom && !member.equals(sender) ) {
                kdc.getMessageManager().send(member, "cmdEnemySuccessMembers", target.getName());
            } else if ( kd == target ) {
                kdc.getMessageManager().send(member, "cmdEnemySuccessMembers", kingdom.getName());
            }
        }
    }
}
