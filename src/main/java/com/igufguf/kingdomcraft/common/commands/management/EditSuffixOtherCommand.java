package com.igufguf.kingdomcraft.common.commands.management;

import com.igufguf.kingdomcraft.api.KingdomCraftPlugin;
import com.igufguf.kingdomcraft.api.commands.CommandSender;
import com.igufguf.kingdomcraft.api.domain.Kingdom;
import com.igufguf.kingdomcraft.api.domain.Player;
import com.igufguf.kingdomcraft.common.commands.DefaultCommandBase;

public class EditSuffixOtherCommand extends DefaultCommandBase {

    public EditSuffixOtherCommand(KingdomCraftPlugin plugin) {
        super(plugin, "edit suffix", 1);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if ( !sender.hasPermission("kingdom.edit.suffix.other") ) {
            plugin.getMessageManager().send(sender, "noPermission");
            return;
        }

        Kingdom kingdom = plugin.getKingdomManager().getKingdom(args[0]);
        if ( kingdom == null ) {
            plugin.getMessageManager().send(sender, "cmdDefaultKingdomNotExist", args[0]);
            return;
        }

        kingdom.setSuffix(args[1]);
        plugin.getKingdomManager().saveKingdom(kingdom);

        plugin.getMessageManager().send(sender, "cmdEditSuccess", "suffix", args[1], kingdom.getName());
    }
}
