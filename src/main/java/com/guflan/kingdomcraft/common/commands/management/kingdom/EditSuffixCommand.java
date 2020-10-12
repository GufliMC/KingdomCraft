package com.guflan.kingdomcraft.common.commands.management.kingdom;

import com.guflan.kingdomcraft.api.KingdomCraft;
import com.guflan.kingdomcraft.api.domain.models.Kingdom;
import com.guflan.kingdomcraft.api.domain.models.User;
import com.guflan.kingdomcraft.api.entity.CommandSender;
import com.guflan.kingdomcraft.api.entity.Player;
import com.guflan.kingdomcraft.common.command.DefaultCommandBase;

public class EditSuffixCommand extends DefaultCommandBase {

    public EditSuffixCommand(KingdomCraft kdc) {
        super(kdc, "edit suffix", 1, true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if ( !sender.hasPermission("kingdom.edit.suffix") ) {
            kdc.getMessageManager().send(sender, "noPermission");
            return;
        }

        User user = kdc.getUser((Player) sender);
        Kingdom kingdom = user.getKingdom();
        if ( kingdom == null ) {
            kdc.getMessageManager().send(sender, "cmdDefaultSenderNoKingdom");
            return;
        }

        kingdom.setSuffix(args[0]);
        kdc.save(kingdom);

        kdc.getMessageManager().send(sender, "cmdEditSuccess", "suffix", args[0]);
    }
}
