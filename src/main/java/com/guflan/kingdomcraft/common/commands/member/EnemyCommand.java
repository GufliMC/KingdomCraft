package com.guflan.kingdomcraft.common.commands.member;

import com.guflan.kingdomcraft.api.KingdomCraft;
import com.guflan.kingdomcraft.api.domain.models.Kingdom;
import com.guflan.kingdomcraft.api.domain.models.Relation;
import com.guflan.kingdomcraft.api.domain.models.RelationType;
import com.guflan.kingdomcraft.api.domain.models.User;
import com.guflan.kingdomcraft.api.entity.CommandSender;
import com.guflan.kingdomcraft.api.entity.Player;
import com.guflan.kingdomcraft.common.command.DefaultCommandBase;

public class EnemyCommand extends DefaultCommandBase {

    public EnemyCommand(KingdomCraft kdc) {
        super(kdc, "enemy", 1, true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if ( !sender.hasPermission("kingdom.enemy") ) {
            kdc.getMessageManager().send(sender, "noPermissionCmd");
            return;
        }

        User user = kdc.getUser((Player) sender);
        Kingdom kingdom = user.getKingdom();
        if ( kingdom == null ) {
            kdc.getMessageManager().send(sender, "cmdDefaultSenderNoKingdom");
            return;
        }

        Kingdom target = kdc.getKingdom(args[0]);
        if ( target == null ) {
            kdc.getMessageManager().send(sender, "cmdDefaultKingdomNotExist", args[0]);
            return;
        }

        Relation existing = kdc.getRelation(kingdom, target);
        if ( existing != null && existing.getType() == RelationType.ENEMY ) {
            kdc.getMessageManager().send(sender, "cmdEnemyAlready", target.getName());
            return;
        }

        kdc.setRelation(kingdom, target, RelationType.ENEMY);
        kdc.getMessageManager().send(sender, "cmdEnemySuccess", target.getName());

        for ( Player member : kdc.getOnlinePlayers() ) {
            Kingdom kd = kdc.getUser(member).getKingdom();
            if ( kd == kingdom && !member.equals(sender) ) {
                kdc.getMessageManager().send(member, "cmdEnemySuccessMembers", target.getName());
            } else if ( kd == target ) {
                kdc.getMessageManager().send(member, "cmdEnemySuccessMembers", kingdom.getName());
            }
        }
    }
}
