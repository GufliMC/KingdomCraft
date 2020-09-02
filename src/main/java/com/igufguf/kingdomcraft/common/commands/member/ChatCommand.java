package com.igufguf.kingdomcraft.common.commands.member;

import com.igufguf.kingdomcraft.api.KingdomCraftPlugin;
import com.igufguf.kingdomcraft.api.chat.ChatChannel;
import com.igufguf.kingdomcraft.api.command.CommandSender;
import com.igufguf.kingdomcraft.api.domain.Kingdom;
import com.igufguf.kingdomcraft.api.domain.Player;
import com.igufguf.kingdomcraft.common.command.DefaultCommandBase;

import java.util.List;
import java.util.stream.Collectors;

public class ChatCommand extends DefaultCommandBase {

    public ChatCommand(KingdomCraftPlugin plugin) {
        super(plugin, "chat", -1, true);
    }

    @Override
    public List<String> autocomplete(CommandSender sender, String[] args) {
        return plugin.getChatManager().getChatChannels().stream().filter(ch -> !ch.isRestricted()).map(ChatChannel::getName).collect(Collectors.toList());
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if ( !sender.hasPermission("kingdom.join") ) {
            plugin.getMessageManager().send(sender, "noPermission");
        }

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

        plugin.getEventManager().kingdomJoin(player);

        // TODO teleport to spawn
		/*
		if ( kingdom.getSpawn() != null && plugin.getCfg().getBoolean("spawn-on-kingdom-join") ) {
			p.teleport(kingdom.getSpawn());
		}
		*/
    }
}
