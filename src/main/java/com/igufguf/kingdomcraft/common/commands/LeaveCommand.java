package com.igufguf.kingdomcraft.common.commands;

import com.igufguf.kingdomcraft.api.KingdomCraftPlugin;
import com.igufguf.kingdomcraft.api.command.CommandSender;
import com.igufguf.kingdomcraft.api.domain.Kingdom;
import com.igufguf.kingdomcraft.api.domain.Player;
import com.igufguf.kingdomcraft.common.command.DefaultCommandBase;

public class LeaveCommand extends DefaultCommandBase {

    public LeaveCommand(KingdomCraftPlugin plugin) {
        super(plugin, "leave", 0, true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = sender.getPlayer();
        if ( player.getKingdom() == null ) {
            plugin.getMessageManager().send(sender, "cmdDefaultSenderNoKingdom");
            return;
        }

        Kingdom kingdom = player.getKingdom();

        plugin.getPlayerManager().leaveKingdom(player);
        plugin.getMessageManager().send(sender, "cmdLeaveSuccess", kingdom.getName());

        for ( Player member : plugin.getKingdomManager().getOnlineMembers(kingdom) ) {
            if ( member == player ) continue;
            plugin.getMessageManager().send(member, "cmdLeaveSuccessMembers", player.getName());
        }

        // TODO teleport to spawn
    }
}
