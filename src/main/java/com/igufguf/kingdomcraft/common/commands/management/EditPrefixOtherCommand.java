package com.igufguf.kingdomcraft.common.commands.management;

import com.igufguf.kingdomcraft.api.KingdomCraftPlugin;
import com.igufguf.kingdomcraft.api.commands.CommandSender;
import com.igufguf.kingdomcraft.api.domain.Kingdom;
import com.igufguf.kingdomcraft.api.domain.Player;
import com.igufguf.kingdomcraft.common.commands.DefaultCommandBase;

public class EditPrefixOtherCommand extends DefaultCommandBase {

    public EditPrefixOtherCommand(KingdomCraftPlugin plugin) {
        super(plugin, "edit prefix", 2);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if ( !sender.hasPermission("kingdom.edit.prefix.other") ) {
            plugin.getMessageManager().send(sender, "noPermission");
            return;
        }

        Kingdom kingdom = plugin.getKingdomManager().getKingdom(args[0]);
        if ( kingdom == null ) {
            plugin.getMessageManager().send(sender, "cmdDefaultKingdomNotExist", args[0]);
            return;
        }

        kingdom.setPrefix(args[1]);
        plugin.getKingdomManager().saveKingdom(kingdom);

        plugin.getMessageManager().send(sender, "cmdEditSuccess", "prefix", args[1], kingdom.getName());
    }
}
