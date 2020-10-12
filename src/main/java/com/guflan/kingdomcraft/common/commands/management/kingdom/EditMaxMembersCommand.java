package com.guflan.kingdomcraft.common.commands.management.kingdom;

import com.guflan.kingdomcraft.api.KingdomCraft;
import com.guflan.kingdomcraft.api.domain.models.Kingdom;
import com.guflan.kingdomcraft.api.domain.models.User;
import com.guflan.kingdomcraft.api.entity.CommandSender;
import com.guflan.kingdomcraft.api.entity.Player;
import com.guflan.kingdomcraft.common.command.DefaultCommandBase;

public class EditMaxMembersCommand extends DefaultCommandBase {

    public EditMaxMembersCommand(KingdomCraft kdc) {
        super(kdc, "edit max-members", 1, true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if ( !sender.hasPermission("kingdom.edit.max-members") ) {
            kdc.getMessageManager().send(sender, "noPermission");
            return;
        }

        User user = kdc.getUser((Player) sender);
        Kingdom kingdom = user.getKingdom();
        if ( kingdom == null ) {
            kdc.getMessageManager().send(sender, "cmdDefaultSenderNoKingdom");
            return;
        }

        if ( !args[0].matches("[0-9]+") ) {
            kdc.getMessageManager().send(sender, "errorInvalidNumber", args[0]);
            return;
        }

        kingdom.setMaxMembers(Integer.parseInt(args[0]));
        kdc.save(kingdom);

        kdc.getMessageManager().send(sender, "cmdEditSuccess", "max-members", kingdom.getMaxMembers() + "");
    }
}
