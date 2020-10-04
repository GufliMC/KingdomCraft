package com.guflan.kingdomcraft.common.commands;

import com.guflan.kingdomcraft.api.KingdomCraftPlugin;
import com.guflan.kingdomcraft.api.domain.Kingdom;
import com.guflan.kingdomcraft.api.domain.User;
import com.guflan.kingdomcraft.api.entity.CommandSender;
import com.guflan.kingdomcraft.api.entity.Player;
import com.guflan.kingdomcraft.common.command.DefaultCommandBase;

import java.util.List;
import java.util.stream.Collectors;

public class JoinCommand extends DefaultCommandBase {

    public JoinCommand(KingdomCraftPlugin plugin) {
        super(plugin, "join", 1, true);
    }

    @Override
    public List<String> autocomplete(CommandSender sender, String[] args) {
        if ( !sender.hasPermission("kingdom.kick.other") ) {
            return null;
        }
        return plugin.getKingdomManager().getKingdoms().stream().map(Kingdom::getName).collect(Collectors.toList());
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

        User player = sender.getPlayer();
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
            if ( member.getPlayer() == player ) continue;
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
