package com.guflan.kingdomcraft.common.commands.management.ranks;

import com.guflan.kingdomcraft.api.KingdomCraft;
import com.guflan.kingdomcraft.api.domain.Kingdom;
import com.guflan.kingdomcraft.api.domain.Rank;
import com.guflan.kingdomcraft.api.entity.CommandSender;
import com.guflan.kingdomcraft.common.command.DefaultCommandBase;

public class RanksEditSuffixOtherCommand extends DefaultCommandBase {

    public RanksEditSuffixOtherCommand(KingdomCraft kdc) {
        super(kdc, "ranks edit suffix", 3);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if ( !sender.hasPermission("kingdom.ranks.edit.suffix.other") ) {
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

        rank.setSuffix(args[2]);
        kdc.save(rank);
        kdc.getMessageManager().send(sender, "cmdRanksEditOtherSuccess", "suffix",
                kingdom.getName(), rank.getName(), args[2]);
    }
}
