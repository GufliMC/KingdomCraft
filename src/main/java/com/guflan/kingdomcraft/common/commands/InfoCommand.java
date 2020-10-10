package com.guflan.kingdomcraft.common.commands;

import com.guflan.kingdomcraft.api.KingdomCraft;
import com.guflan.kingdomcraft.api.domain.Kingdom;
import com.guflan.kingdomcraft.api.domain.User;
import com.guflan.kingdomcraft.api.entity.CommandSender;
import com.guflan.kingdomcraft.api.entity.Player;
import com.guflan.kingdomcraft.common.command.DefaultCommandBase;

public class InfoCommand extends DefaultCommandBase {

    public InfoCommand(KingdomCraft kdc) {
        super(kdc, "info", 0, true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if ( !sender.hasPermission("kingdom.info") ) {
            kdc.getMessageManager().send(sender, "noPermission");
        }

        User user = kdc.getUser((Player) sender);
        sender.sendMessage("kingdom: " + (user.getKingdom() == null ? "null" : user.getKingdom().getDisplay()));
        sender.sendMessage("rank: " + (user.getRank() == null ? "null" : user.getRank().getDisplay()));
    }
}
