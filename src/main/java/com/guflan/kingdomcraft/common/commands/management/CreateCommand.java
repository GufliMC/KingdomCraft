package com.guflan.kingdomcraft.common.commands.management;

import com.guflan.kingdomcraft.api.KingdomCraft;
import com.guflan.kingdomcraft.api.domain.Kingdom;
import com.guflan.kingdomcraft.api.domain.User;
import com.guflan.kingdomcraft.api.entity.CommandSender;
import com.guflan.kingdomcraft.api.entity.Player;
import com.guflan.kingdomcraft.common.command.DefaultCommandBase;

public class CreateCommand extends DefaultCommandBase {

    public CreateCommand(KingdomCraft kdc) {
        super(kdc, "create", 1);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if ( !sender.hasPermission("kingdom.create") ) {
            kdc.getMessageManager().send(sender, "noPermission");
        }

        if ( !args[0].matches("[a-zA-Z0-9]+") ) {
            kdc.getMessageManager().send(sender, "cmdCreateNameInvalid", args[0]);
            return;
        }

        if ( kdc.getKingdom(args[0]) != null ) {
            kdc.getMessageManager().send(sender, "cmdCreateAlreadyExists", args[0]);
            return;
        }

        Kingdom kingdom = kdc.createKingdom(args[0]);
        kdc.getMessageManager().send(sender, "cmdCreateSuccess", kingdom.getName());

        // place player in created kingdom (TODO is this necessary?)
        if ( sender instanceof Player) {
            User user = kdc.getUser((Player) sender);
            user.setKingdom(kingdom);
            kdc.save(user);
        }
    }
}
