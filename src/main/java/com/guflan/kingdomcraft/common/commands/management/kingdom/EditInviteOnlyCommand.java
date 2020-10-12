package com.guflan.kingdomcraft.common.commands.management.kingdom;

import com.guflan.kingdomcraft.api.KingdomCraft;
import com.guflan.kingdomcraft.api.domain.models.Kingdom;
import com.guflan.kingdomcraft.api.domain.models.User;
import com.guflan.kingdomcraft.api.entity.CommandSender;
import com.guflan.kingdomcraft.api.entity.Player;
import com.guflan.kingdomcraft.common.command.DefaultCommandBase;

public class EditInviteOnlyCommand extends DefaultCommandBase {

    public EditInviteOnlyCommand(KingdomCraft kdc) {
        super(kdc, "edit invite-only", 1, true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if ( !sender.hasPermission("kingdom.edit.invite-only") ) {
            kdc.getMessageManager().send(sender, "noPermission");
            return;
        }

        User user = kdc.getUser((Player) sender);
        Kingdom kingdom = user.getKingdom();
        if ( kingdom == null ) {
            kdc.getMessageManager().send(sender, "cmdDefaultSenderNoKingdom");
            return;
        }

        if ( !args[0].equalsIgnoreCase("true")
                && !args[0].equalsIgnoreCase("false") ) {
            kdc.getMessageManager().send(sender, "errorInvalidBoolean", args[0]);
            return;
        }

        kingdom.setInviteOnly(Boolean.parseBoolean(args[0]));
        kdc.save(kingdom);

        kdc.getMessageManager().send(sender, "cmdEditSuccess", "invite-only", kingdom.isInviteOnly() + "");
    }
}
