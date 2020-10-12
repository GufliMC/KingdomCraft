package com.guflan.kingdomcraft.common.commands.management.kingdom;

import com.guflan.kingdomcraft.api.KingdomCraft;
import com.guflan.kingdomcraft.api.domain.models.Kingdom;
import com.guflan.kingdomcraft.api.entity.CommandSender;
import com.guflan.kingdomcraft.common.command.DefaultCommandBase;

import java.util.List;
import java.util.stream.Collectors;

public class EditSuffixOtherCommand extends DefaultCommandBase {

    public EditSuffixOtherCommand(KingdomCraft kdc) {
        super(kdc, "edit suffix", 1);
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
        if ( !sender.hasPermission("kingdom.edit.suffix.other") ) {
            kdc.getMessageManager().send(sender, "noPermission");
            return;
        }

        Kingdom kingdom = kdc.getKingdom(args[0]);
        if ( kingdom == null ) {
            kdc.getMessageManager().send(sender, "cmdDefaultKingdomNotExist", args[0]);
            return;
        }

        kingdom.setSuffix(args[1]);
        kdc.save(kingdom);

        kdc.getMessageManager().send(sender, "cmdEditOtherSuccess", "suffix", kingdom.getName(), args[1]);
    }
}
