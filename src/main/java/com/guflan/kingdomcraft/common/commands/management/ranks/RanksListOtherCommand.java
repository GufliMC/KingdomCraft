package com.guflan.kingdomcraft.common.commands.management.ranks;

import com.guflan.kingdomcraft.api.KingdomCraft;
import com.guflan.kingdomcraft.api.domain.Kingdom;
import com.guflan.kingdomcraft.api.domain.Rank;
import com.guflan.kingdomcraft.api.domain.User;
import com.guflan.kingdomcraft.api.entity.CommandSender;
import com.guflan.kingdomcraft.api.entity.Player;
import com.guflan.kingdomcraft.common.command.DefaultCommandBase;

import java.util.stream.Collectors;

public class RanksListOtherCommand extends DefaultCommandBase {

    public RanksListOtherCommand(KingdomCraft kdc) {
        super(kdc, "ranks list", 1);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if ( !sender.hasPermission("kingdom.ranks.list.other") ) {
            kdc.getMessageManager().send(sender, "noPermission");
            return;
        }

        Kingdom kingdom = kdc.getKingdom(args[0]);
        if ( kingdom == null ) {
            kdc.getMessageManager().send(sender, "cmdDefaultKingdomNotExist", args[0]);
            return;
        }

        String list = kingdom.getRanks().stream()
                .sorted((o1, o2) -> o2.getLevel() - o1.getLevel())
                .map(Rank::getName)
                .collect(Collectors.joining(", "));
        kdc.getMessageManager().send(sender, "cmdRanksListOther", kingdom.getName(), list);
    }
}
