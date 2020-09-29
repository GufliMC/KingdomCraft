package com.guflan.kingdomcraft.common.commands.management;

import com.guflan.kingdomcraft.api.KingdomCraftPlugin;
import com.guflan.kingdomcraft.api.domain.Kingdom;
import com.guflan.kingdomcraft.api.entity.CommandSender;
import com.guflan.kingdomcraft.common.command.DefaultCommandBase;

public class CreateCommand extends DefaultCommandBase {

    public CreateCommand(KingdomCraftPlugin plugin) {
        super(plugin, "create", 1);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if ( !sender.hasPermission("kingdom.create") ) {
            plugin.getMessageManager().send(sender, "noPermission");
        }

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
            // TODO is this necessary? maybe an option?
            sender.getPlayer().setKingdom(kingdom);
        }
    }
}
