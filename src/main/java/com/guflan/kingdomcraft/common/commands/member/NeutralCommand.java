package com.guflan.kingdomcraft.common.commands.member;

import com.guflan.kingdomcraft.api.KingdomCraft;
import com.guflan.kingdomcraft.api.domain.models.Kingdom;
import com.guflan.kingdomcraft.api.domain.models.Relation;
import com.guflan.kingdomcraft.api.domain.models.RelationType;
import com.guflan.kingdomcraft.api.domain.models.User;
import com.guflan.kingdomcraft.api.entity.CommandSender;
import com.guflan.kingdomcraft.api.entity.Player;
import com.guflan.kingdomcraft.common.command.DefaultCommandBase;

public class NeutralCommand extends DefaultCommandBase {

    public NeutralCommand(KingdomCraft kdc) {
        super(kdc, "neutral", 1, true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if ( !sender.hasPermission("kingdom.neutral") ) {
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

                for (Player member : kdc.getOnlinePlayers()) {
                    if (kdc.getUser(member).getKingdom() != target) continue;
                    kdc.getMessageManager().send(sender, "cmdNeutralRequestTarget", kingdom.getName());
                }

                return;
            }
        }

        kdc.setRelation(target, kingdom, RelationType.NEUTRAL);
        kdc.getMessageManager().send(sender, "cmdNeutralAccepted", target.getName());

        for ( Player member : kdc.getOnlinePlayers() ) {
            if ( kdc.getUser(member).getKingdom() != target ) continue;
            kdc.getMessageManager().send(sender, "cmdNeutralAccepted", kingdom.getName());
        }
    }
}
