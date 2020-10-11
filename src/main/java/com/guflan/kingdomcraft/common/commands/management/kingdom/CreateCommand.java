package com.guflan.kingdomcraft.common.commands.management.kingdom;

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
        if ( !sender.hasPermission("kingdom.create") && !sender.hasPermission("kingdom.create.other") ) {
            kdc.getMessageManager().send(sender, "noPermissionCmd");
            return;
        }

        if ( !args[0].matches("[a-zA-Z0-9]+") ) {
            kdc.getMessageManager().send(sender, "cmdCreateNameInvalid");
            return;
        }

        if ( kdc.getKingdom(args[0]) != null ) {
            kdc.getMessageManager().send(sender, "cmdCreateAlreadyExists", args[0]);
            return;
        }

        if ( sender instanceof Player && kdc.getUser((Player) sender).getKingdom() != null
                && !sender.hasPermission("kingdom.create.other") ) {
            kdc.getMessageManager().send(sender, "noPermissionCmd");
            return;
        }

        Kingdom kingdom = kdc.createKingdom(args[0]);
        kdc.getMessageManager().send(sender, "cmdCreateSuccess", kingdom.getName());

        if ( sender instanceof Player && kdc.getUser((Player) sender).getKingdom() == null ) {
            User user = kdc.getUser((Player) sender);
            user.setKingdom(kingdom);
            kdc.save(kingdom).thenRun(() -> kdc.save(user));
        }
    }
}
