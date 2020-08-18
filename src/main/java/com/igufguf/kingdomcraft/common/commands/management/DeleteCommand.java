package com.igufguf.kingdomcraft.common.commands.management;

import com.igufguf.kingdomcraft.api.KingdomCraftPlugin;
import com.igufguf.kingdomcraft.api.command.CommandSender;
import com.igufguf.kingdomcraft.api.domain.Kingdom;
import com.igufguf.kingdomcraft.api.domain.Player;
import com.igufguf.kingdomcraft.common.command.DefaultCommandBase;

public class DeleteCommand extends DefaultCommandBase {

    public DeleteCommand(KingdomCraftPlugin plugin) {
        super(plugin, "delete", 1);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Kingdom kingdom = plugin.getKingdomManager().getKingdom(args[0]);
        if ( kingdom == null ) {
            plugin.getMessageManager().send(sender, "cmdDefaultKingdomNotExist", args[0]);
            return;
        }

        if ( !sender.isConsole() ) {
            Player player = sender.getPlayer();

            // other kingdom
            if (player.getKingdom() != kingdom && !sender.hasPermission("kingdom.delete.other")) {
                plugin.getMessageManager().send(sender, "noPermission");
                return;
            }

            // own kingdom
            if (player.getKingdom() == kingdom && !sender.hasPermission("kingdom.delete")) {
                plugin.getMessageManager().send(sender, "noPermission");
                return;
            }
        }

        plugin.getKingdomManager().deleteKingdom(kingdom);
        plugin.getMessageManager().send(sender, "cmdDeleteSuccess", kingdom.getName());
    }
}
