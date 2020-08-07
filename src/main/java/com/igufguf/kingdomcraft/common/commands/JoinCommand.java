package com.igufguf.kingdomcraft.common.commands;

import com.igufguf.kingdomcraft.api.KingdomCraftPlugin;
import com.igufguf.kingdomcraft.api.commands.CommandSender;
import com.igufguf.kingdomcraft.api.domain.Kingdom;
import com.igufguf.kingdomcraft.api.domain.Player;

public class JoinCommand extends DefaultCommandBase {

    public JoinCommand(KingdomCraftPlugin plugin) {
        super(plugin, "join", 1, true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Kingdom kingdom = plugin.getKingdomManager().getKingdom(args[0]);
        if ( kingdom == null ) {
            plugin.getMessageManager().send(sender, "cmdDefaultKingdomNotExist", args[0]);
            return;
        }

        Player player = sender.getPlayer();
        if ( player.getKingdom() != null ) {
            plugin.getMessageManager().send(sender, "cmdJoinAlready");
            return;
        }

        if ( kingdom.isInviteOnly() ) {
            plugin.getMessageManager().send(sender, "cmdJoinInviteOnly", kingdom.getName());

            // TODO check invite
            return;
        }

        // TODO check for max members


        plugin.getPlayerManager().joinKingdom(player, kingdom);
        plugin.getMessageManager().send(sender, "cmdJoinSuccess", kingdom.getName());

        for ( Player member : plugin.getKingdomManager().getOnlineMembers(kingdom) ) {
            if ( member == player ) continue;
            plugin.getMessageManager().send(member, "cmdJoinSuccessMembers", player.getName());
        }

        // TODO teleport to spawn
		/*
		if ( kingdom.getSpawn() != null && plugin.getCfg().getBoolean("spawn-on-kingdom-join") ) {
			p.teleport(kingdom.getSpawn());
		}
		*/
    }
}
