package com.guflan.kingdomcraft.common.commands.member;

import com.guflan.kingdomcraft.api.KingdomCraft;
import com.guflan.kingdomcraft.api.chat.ChatChannel;
import com.guflan.kingdomcraft.api.domain.Kingdom;
import com.guflan.kingdomcraft.api.domain.User;
import com.guflan.kingdomcraft.api.entity.CommandSender;
import com.guflan.kingdomcraft.api.entity.Player;
import com.guflan.kingdomcraft.common.command.DefaultCommandBase;

import java.util.List;
import java.util.stream.Collectors;

public class ChatCommand extends DefaultCommandBase {

    public ChatCommand(KingdomCraft bridge) {
        super(bridge, "chat", -1, true);
    }

    @Override
    public List<String> autocomplete(CommandSender sender, String[] args) {
        return bridge.getChatManager().getChatChannels().stream().filter(ch -> !ch.isRestricted()).map(ChatChannel::getName).collect(Collectors.toList());
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        if ( !player.hasPermission("kingdom.join") ) {
            bridge.getMessageManager().send(sender, "noPermission");
        }

        Kingdom kingdom = bridge.getKingdomManager().getKingdom(args[0]);
        if ( kingdom == null ) {
            bridge.getMessageManager().send(sender, "cmdDefaultKingdomNotExist", args[0]);
            return;
        }

        User user = bridge.getUserManager().getUser(player.getUniqueId());
        if ( user.getKingdom() != null ) {
            bridge.getMessageManager().send(sender, "cmdJoinAlready");
            return;
        }

        if ( kingdom.isInviteOnly() && !user.hasInvite(kingdom) ) {
            bridge.getMessageManager().send(sender, "cmdJoinInviteOnly", kingdom.getName());
            return;
        }

        // TODO check for max members

        user.setKingdom(kingdom);
        bridge.getMessageManager().send(sender, "cmdJoinSuccess", kingdom.getName());

        for ( Player p : bridge.getOnlinePlayers() ) {
            if ( p == player || bridge.getUserManager().getUser(p.getUniqueId()).getKingdom() != kingdom ) continue;
            bridge.getMessageManager().send(p, "cmdJoinSuccessMembers", player.getName());
        }

        bridge.getEventManager().kingdomJoin(user);

        // TODO teleport to spawn
		/*
		if ( kingdom.getSpawn() != null && plugin.getCfg().getBoolean("spawn-on-kingdom-join") ) {
			p.teleport(kingdom.getSpawn());
		}
		*/
    }
}
