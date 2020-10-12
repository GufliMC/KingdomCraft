package com.guflan.kingdomcraft.common.commands.management.ranks;

import com.guflan.kingdomcraft.api.KingdomCraft;
import com.guflan.kingdomcraft.api.domain.models.Kingdom;
import com.guflan.kingdomcraft.api.domain.models.Rank;
import com.guflan.kingdomcraft.api.entity.CommandSender;
import com.guflan.kingdomcraft.common.command.DefaultCommandBase;

public class RanksEditMaxMembersOtherCommand extends DefaultCommandBase {

    public RanksEditMaxMembersOtherCommand(KingdomCraft kdc) {
        super(kdc, "ranks edit max-members", 3);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if ( !sender.hasPermission("kingdom.ranks.edit.max-members.other") ) {
            kdc.getMessageManager().send(sender, "noPermission");
            return;
        }

        Kingdom kingdom = kdc.getKingdom(args[0]);
        if ( kingdom == null ) {
            kdc.getMessageManager().send(sender, "cmdDefaultKingdomNotExist", args[0]);
            return;
        }

        Rank rank = kingdom.getRank(args[1]);
        if ( rank == null ) {
            kdc.getMessageManager().send(sender, "cmdDefaultRankNotExist", args[1]);
            return;
        }

        if ( !args[2].matches("[0-9]+") ) {
            kdc.getMessageManager().send(sender, "errorInvalidNumber", args[2]);
            return;
        }

        rank.setMaxMembers(Integer.parseInt(args[2]));
        kdc.save(rank);
        kdc.getMessageManager().send(sender, "cmdRanksEditOtherSuccess", "max-members",
                kingdom.getName(), rank.getName(), args[2]);
    }
}
