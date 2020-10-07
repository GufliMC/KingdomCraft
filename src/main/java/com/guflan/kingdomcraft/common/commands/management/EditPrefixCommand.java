package com.guflan.kingdomcraft.common.commands.management;

import com.guflan.kingdomcraft.api.KingdomCraft;
import com.guflan.kingdomcraft.api.domain.Kingdom;
import com.guflan.kingdomcraft.api.domain.User;
import com.guflan.kingdomcraft.api.entity.CommandSender;
import com.guflan.kingdomcraft.api.entity.Player;
import com.guflan.kingdomcraft.common.command.DefaultCommandBase;

public class EditPrefixCommand extends DefaultCommandBase {

    public EditPrefixCommand(KingdomCraft kdc) {
        super(kdc, "edit prefix", 1, true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if ( !sender.hasPermission("kingdom.edit.prefix") ) {
            kdc.getMessageManager().send(sender, "noPermission");
            return;
        }

        User user = kdc.getUser((Player) sender);
        Kingdom kingdom = user.getKingdom();
        if ( kingdom == null ) {
            kdc.getMessageManager().send(sender, "cmdDefaultSenderNoKingdom");
            return;
        }

        kingdom.setPrefix(args[0]);
        kdc.save(kingdom);

        kdc.getMessageManager().send(sender, "cmdEditSuccess", "prefix", args[0], kingdom.getName());
    }
}
