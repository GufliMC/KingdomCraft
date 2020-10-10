package com.guflan.kingdomcraft.common.commands.management;

import com.guflan.kingdomcraft.api.KingdomCraft;
import com.guflan.kingdomcraft.api.domain.Kingdom;
import com.guflan.kingdomcraft.api.domain.Rank;
import com.guflan.kingdomcraft.api.domain.User;
import com.guflan.kingdomcraft.api.entity.CommandSender;
import com.guflan.kingdomcraft.api.entity.Player;
import com.guflan.kingdomcraft.common.command.DefaultCommandBase;

import java.util.stream.Collectors;

public class RanksListCommand extends DefaultCommandBase {

    public RanksListCommand(KingdomCraft kdc) {
        super(kdc, "ranks list", 0, true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if ( !sender.hasPermission("kingdom.ranks.list") ) {
            kdc.getMessageManager().send(sender, "noPermission");
        }

        User user = kdc.getUser((Player) sender);
        Kingdom kingdom = user.getKingdom();
        if ( kingdom == null ) {
            kdc.getMessageManager().send(sender, "cmdDefaultSenderNoKingdom");
            return;
        }

        sender.sendMessage(kingdom.getRanks().stream().map(Rank::getName).collect(Collectors.joining(", ")));
        //kdc.getMessageManager().send(sender, "cmdCreateSuccess", kingdom.getName());

    }
}
