package com.guflan.kingdomcraft.common.commands.management.kingdom;

import com.guflan.kingdomcraft.api.KingdomCraft;
import com.guflan.kingdomcraft.api.domain.models.Kingdom;
import com.guflan.kingdomcraft.api.entity.CommandSender;
import com.guflan.kingdomcraft.common.command.DefaultCommandBase;

import java.util.List;
import java.util.stream.Collectors;

public class EditDisplayOtherCommand extends DefaultCommandBase {

    public EditDisplayOtherCommand(KingdomCraft kdc) {
        super(kdc, "edit display", 2);
    }

    @Override
    public List<String> autocomplete(CommandSender sender, String[] args) {
        if ( args.length == 1 ) {
            return kdc.getKingdoms().stream().map(Kingdom::getName).collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if ( !sender.hasPermission("kingdom.edit.display.other") ) {
            kdc.getMessageManager().send(sender, "noPermission");
            return;
        }

        Kingdom kingdom = kdc.getKingdom(args[0]);
        if ( kingdom == null ) {
            kdc.getMessageManager().send(sender, "cmdDefaultKingdomNotExist", args[0]);
            return;
        }

        kingdom.setDisplay(args[1]);
        kdc.save(kingdom);

        kdc.getMessageManager().send(sender, "cmdEditOtherSuccess", "display", kingdom.getName(), args[1]);
    }
}
