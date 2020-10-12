package com.guflan.kingdomcraft.common.commands.management.kingdom;

import com.guflan.kingdomcraft.api.KingdomCraft;
import com.guflan.kingdomcraft.api.domain.models.Kingdom;
import com.guflan.kingdomcraft.api.entity.CommandSender;
import com.guflan.kingdomcraft.common.command.DefaultCommandBase;

public class EditInviteOnlyOtherCommand extends DefaultCommandBase {

    public EditInviteOnlyOtherCommand(KingdomCraft kdc) {
        super(kdc, "edit invite-only", 2);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if ( !sender.hasPermission("kingdom.edit.invite-only.other") ) {
            kdc.getMessageManager().send(sender, "noPermission");
            return;
        }

        Kingdom kingdom = kdc.getKingdom(args[0]);
        if ( kingdom == null ) {
            kdc.getMessageManager().send(sender, "cmdDefaultKingdomNotExist", args[0]);
            return;
        }

        if ( !args[1].equalsIgnoreCase("true")
                && !args[1].equalsIgnoreCase("false") ) {
            kdc.getMessageManager().send(sender, "errorInvalidBoolean", args[1]);
            return;
        }

        kingdom.setInviteOnly(Boolean.parseBoolean(args[1]));
        kdc.save(kingdom);

        kdc.getMessageManager().send(sender, "cmdEditOtherSuccess", "invite-only", kingdom.getName(), kingdom.isInviteOnly() + "");
    }
}
