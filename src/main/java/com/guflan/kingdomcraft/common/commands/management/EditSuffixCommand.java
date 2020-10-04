package com.guflan.kingdomcraft.common.commands.management;

import com.guflan.kingdomcraft.api.KingdomCraftPlugin;
import com.guflan.kingdomcraft.api.domain.Kingdom;
import com.guflan.kingdomcraft.api.domain.User;
import com.guflan.kingdomcraft.api.entity.CommandSender;
import com.guflan.kingdomcraft.common.command.DefaultCommandBase;

public class EditSuffixCommand extends DefaultCommandBase {

    public EditSuffixCommand(KingdomCraftPlugin plugin) {
        super(plugin, "edit suffix", 1, true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if ( !sender.hasPermission("kingdom.edit.suffix") ) {
            plugin.getMessageManager().send(sender, "noPermission");
            return;
        }

        User player = sender.getPlayer();
        Kingdom kingdom = player.getKingdom();
        if ( kingdom == null ) {
            plugin.getMessageManager().send(sender, "cmdDefaultSenderNoKingdom");
            return;
        }

        kingdom.setSuffix(args[0]);
        plugin.getKingdomManager().saveKingdom(kingdom);

        plugin.getMessageManager().send(sender, "cmdEditSuccess", "suffix", args[0], kingdom.getName());
    }
}
