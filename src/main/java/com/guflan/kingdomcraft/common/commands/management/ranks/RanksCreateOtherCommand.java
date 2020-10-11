package com.guflan.kingdomcraft.common.commands.management.ranks;

import com.guflan.kingdomcraft.api.KingdomCraft;
import com.guflan.kingdomcraft.api.domain.Kingdom;
import com.guflan.kingdomcraft.api.domain.Rank;
import com.guflan.kingdomcraft.api.domain.User;
import com.guflan.kingdomcraft.api.entity.CommandSender;
import com.guflan.kingdomcraft.api.entity.Player;
import com.guflan.kingdomcraft.common.command.DefaultCommandBase;

public class RanksCreateOtherCommand extends DefaultCommandBase {

    public RanksCreateOtherCommand(KingdomCraft kdc) {
        super(kdc, "ranks create", 2);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if ( !sender.hasPermission("kingdom.ranks.create.other") ) {
            kdc.getMessageManager().send(sender, "noPermission");
            return;
        }

        if ( !args[1].matches("[a-zA-Z0-9]+") ) {
            kdc.getMessageManager().send(sender, "cmdRanksCreateNameInvalid");
            return;
        }

        Kingdom kingdom = kdc.getKingdom(args[0]);
        if ( kingdom == null ) {
            kdc.getMessageManager().send(sender, "cmdDefaultKingdomNotExist", args[0]);
            return;
        }

        if ( kingdom.getRank(args[1]) != null ) {
            kdc.getMessageManager().send(sender, "cmdRanksCreateAlreadyExists", args[1]);
            return;
        }

        Rank rank = kingdom.createRank(args[1]);
        kdc.save(rank).thenRun(() -> {
            if (kingdom.getDefaultRank() == null) {
                kingdom.setDefaultRank(rank);
                kdc.save(kingdom);
            }
        });

        kdc.getMessageManager().send(sender, "cmdRanksCreateSuccess", rank.getName());
    }
}
