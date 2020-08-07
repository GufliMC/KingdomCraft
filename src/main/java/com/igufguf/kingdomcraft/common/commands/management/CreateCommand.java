package com.igufguf.kingdomcraft.common.commands.management;

import com.igufguf.kingdomcraft.api.KingdomCraftPlugin;
import com.igufguf.kingdomcraft.api.commands.CommandSender;
import com.igufguf.kingdomcraft.api.domain.Kingdom;
import com.igufguf.kingdomcraft.api.domain.Player;
import com.igufguf.kingdomcraft.common.commands.DefaultCommandBase;

public class CreateCommand extends DefaultCommandBase {

    public CreateCommand(KingdomCraftPlugin plugin) {
        super(plugin, "create", 1);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if ( !args[0].matches("[a-zA-Z0-9]+") ) {
            plugin.getMessageManager().send(sender, "cmdCreateNameInvalid", args[0]);
            return;
        }

        if ( plugin.getKingdomManager().getKingdom(args[0]) != null ) {
            plugin.getMessageManager().send(sender, "cmdCreateAlreadyExists", args[0]);
            return;
        }

        Kingdom kingdom = plugin.getKingdomManager().createKingdom(args[0]);
        plugin.getMessageManager().send(sender, "cmdCreateSuccess", kingdom.getName());

        if ( !sender.isConsole() ) {
            sender.getPlayer().setKingdom(kingdom);
        }

        plugin.getMessageManager().send(sender, "cmdCreateSuccess", kingdom.getName());
    }
}
