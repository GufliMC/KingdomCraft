package com.guflan.kingdomcraft.common.commands.member;

import com.guflan.kingdomcraft.api.KingdomCraft;
import com.guflan.kingdomcraft.api.domain.models.Kingdom;
import com.guflan.kingdomcraft.api.domain.models.Relation;
import com.guflan.kingdomcraft.api.domain.models.RelationType;
import com.guflan.kingdomcraft.api.domain.models.User;
import com.guflan.kingdomcraft.api.entity.CommandSender;
import com.guflan.kingdomcraft.api.entity.Player;
import com.guflan.kingdomcraft.common.command.DefaultCommandBase;

public class AllyCommand extends DefaultCommandBase {

    public AllyCommand(KingdomCraft kdc) {
        super(kdc, "ally", 1, true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if ( !sender.hasPermission("kingdom.ally") ) {
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
        if ( existing != null && existing.getType() == RelationType.ALLY ) {
            kdc.getMessageManager().send(sender, "cmdAllyAlready", target.getName());
            return;
        }

        Relation request = kdc.getRelationRequest(target, kingdom);
        if ( request == null || request.getType() != RelationType.ALLY ) {

            request = kdc.getRelationRequest(kingdom, target);
            if ( request != null && request.getType() == RelationType.ALLY ) {
                kdc.getMessageManager().send(sender, "cmdAllyRequestAlready", target.getName());
                return;
            }

            kdc.addRelationRequest(kingdom, target, RelationType.ALLY);
            kdc.getMessageManager().send(sender, "cmdAllyRequest", target.getName());

            for ( Player member : kdc.getOnlinePlayers() ) {
                if ( kdc.getUser(member).getKingdom() != target ) continue;
                kdc.getMessageManager().send(sender, "cmdAllyRequestTarget", kingdom.getName());
            }
            return;
        }

        kdc.removeRelationRequest(target, kingdom);
        kdc.setRelation(target, kingdom, RelationType.ALLY);
        kdc.getMessageManager().send(sender, "cmdAllyAccepted", target.getName());

        for ( Player member : kdc.getOnlinePlayers() ) {
            if ( kdc.getUser(member).getKingdom() != target ) continue;
            kdc.getMessageManager().send(sender, "cmdAllyAccepted", kingdom.getName());
        }
    }
}
