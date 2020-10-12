package com.guflan.kingdomcraft.common.commands.management.ranks;

import com.guflan.kingdomcraft.api.KingdomCraft;
import com.guflan.kingdomcraft.api.domain.models.Kingdom;
import com.guflan.kingdomcraft.api.domain.models.Rank;
import com.guflan.kingdomcraft.api.domain.models.User;
import com.guflan.kingdomcraft.api.entity.CommandSender;
import com.guflan.kingdomcraft.api.entity.Player;
import com.guflan.kingdomcraft.common.command.DefaultCommandBase;

public class RanksEditSuffixCommand extends DefaultCommandBase {

    public RanksEditSuffixCommand(KingdomCraft kdc) {
        super(kdc, "ranks edit suffix", 2, true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if ( !sender.hasPermission("kingdom.ranks.edit.suffix") ) {
            kdc.getMessageManager().send(sender, "noPermission");
            return;
        }

        User user = kdc.getUser((Player) sender);
        Kingdom kingdom = user.getKingdom();
        if ( kingdom == null ) {
            kdc.getMessageManager().send(sender, "cmdDefaultSenderNoKingdom");
            return;
        }

        Rank rank = kingdom.getRank(args[0]);
        if ( rank == null ) {
            kdc.getMessageManager().send(sender, "cmdDefaultRankNotExist", args[0]);
            return;
        }

        rank.setSuffix(args[1]);
        kdc.save(rank);
        kdc.getMessageManager().send(sender, "cmdRanksEditSuccess", "suffix", rank.getName(), args[1]);
    }
}
