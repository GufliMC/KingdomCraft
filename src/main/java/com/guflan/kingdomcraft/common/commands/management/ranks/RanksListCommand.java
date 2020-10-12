package com.guflan.kingdomcraft.common.commands.management.ranks;

import com.guflan.kingdomcraft.api.KingdomCraft;
import com.guflan.kingdomcraft.api.domain.models.Kingdom;
import com.guflan.kingdomcraft.api.domain.models.Rank;
import com.guflan.kingdomcraft.api.domain.models.User;
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
            return;
        }

        User user = kdc.getUser((Player) sender);
        Kingdom kingdom = user.getKingdom();
        if ( kingdom == null ) {
            kdc.getMessageManager().send(sender, "cmdDefaultSenderNoKingdom");
            return;
        }

        String list = kingdom.getRanks().stream()
                .sorted((o1, o2) -> o2.getLevel() - o1.getLevel())
                .map(Rank::getName)
                .collect(Collectors.joining(", "));
        kdc.getMessageManager().send(sender, "cmdRanksList", list);
    }
}
