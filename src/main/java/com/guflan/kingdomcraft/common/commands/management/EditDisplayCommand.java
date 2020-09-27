package com.guflan.kingdomcraft.common.commands.management;

import com.guflan.kingdomcraft.api.KingdomCraftPlugin;
import com.guflan.kingdomcraft.api.command.CommandSender;
import com.guflan.kingdomcraft.api.domain.Kingdom;
import com.guflan.kingdomcraft.api.domain.Player;
import com.guflan.kingdomcraft.common.command.DefaultCommandBase;

public class EditDisplayCommand extends DefaultCommandBase {

    public EditDisplayCommand(KingdomCraftPlugin plugin) {
        super(plugin, "edit display", 1, true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if ( !sender.hasPermission("kingdom.edit.display") ) {
            plugin.getMessageManager().send(sender, "noPermission");
            return;
        }

        Player player = sender.getPlayer();
        Kingdom kingdom = player.getKingdom();
        if ( kingdom == null ) {
            plugin.getMessageManager().send(sender, "cmdDefaultSenderNoKingdom");
            return;
        }

        kingdom.setDisplay(args[0]);
        plugin.getKingdomManager().saveKingdom(kingdom);

        plugin.getMessageManager().send(sender, "cmdEditSuccess", "display", args[0], kingdom.getName());
    }
}
