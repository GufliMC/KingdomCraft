package com.guflan.kingdomcraft.common.commands.management.ranks;

import com.guflan.kingdomcraft.api.KingdomCraft;
import com.guflan.kingdomcraft.api.domain.Kingdom;
import com.guflan.kingdomcraft.api.domain.Rank;
import com.guflan.kingdomcraft.api.domain.User;
import com.guflan.kingdomcraft.api.entity.CommandSender;
import com.guflan.kingdomcraft.api.entity.Player;
import com.guflan.kingdomcraft.common.command.DefaultCommandBase;

public class RanksCreateCommand extends DefaultCommandBase {

    public RanksCreateCommand(KingdomCraft kdc) {
        super(kdc, "ranks create", 1, true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if ( !sender.hasPermission("kingdom.ranks.create") ) {
            kdc.getMessageManager().send(sender, "noPermission");
            return;
        }

        if ( !args[0].matches("[a-zA-Z0-9]+") ) {
            kdc.getMessageManager().send(sender, "cmdRanksCreateNameInvalid");
            return;
        }

        User user = kdc.getUser((Player) sender);
        Kingdom kingdom = user.getKingdom();
        if ( kingdom == null ) {
            kdc.getMessageManager().send(sender, "cmdDefaultSenderNoKingdom");
            return;
        }

        if ( kingdom.getRank(args[0]) != null ) {
            kdc.getMessageManager().send(sender, "cmdRanksCreateAlreadyExists", args[0]);
            return;
        }

        Rank rank = kingdom.createRank(args[0]);
        kdc.save(rank).thenRun(() -> {
            if (kingdom.getDefaultRank() == null) {
                kingdom.setDefaultRank(rank);
                kdc.save(kingdom);
            }
        });

        kdc.getMessageManager().send(sender, "cmdRanksCreateSuccess", rank.getName());
    }
}
