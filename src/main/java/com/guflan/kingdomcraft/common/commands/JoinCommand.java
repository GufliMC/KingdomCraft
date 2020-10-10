package com.guflan.kingdomcraft.common.commands;

import com.guflan.kingdomcraft.api.KingdomCraft;
import com.guflan.kingdomcraft.api.domain.Kingdom;
import com.guflan.kingdomcraft.api.domain.User;
import com.guflan.kingdomcraft.api.entity.CommandSender;
import com.guflan.kingdomcraft.api.entity.Player;
import com.guflan.kingdomcraft.common.command.DefaultCommandBase;

import java.util.List;
import java.util.stream.Collectors;

public class JoinCommand extends DefaultCommandBase {

    public JoinCommand(KingdomCraft plugin) {
        super(plugin, "join", 1, true);
    }

    @Override
    public List<String> autocomplete(CommandSender sender, String[] args) {
        if ( !sender.hasPermission("kingdom.join") ) {
            return null;
        }
        return kdc.getKingdoms().stream().map(Kingdom::getName).collect(Collectors.toList());
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if ( !sender.hasPermission("kingdom.join") ) {
            kdc.getMessageManager().send(sender, "noPermission");
        }

        Kingdom kingdom = kdc.getKingdom(args[0]);
        if ( kingdom == null ) {
            kdc.getMessageManager().send(sender, "cmdDefaultKingdomNotExist", args[0]);
            return;
        }

        User user = kdc.getUser((Player) sender);
        if ( user.getKingdom() != null ) {
            kdc.getMessageManager().send(sender, "cmdJoinAlready");
            return;
        }

        if ( kingdom.isInviteOnly() && !user.hasInvite(kingdom) ) {
            kdc.getMessageManager().send(sender, "cmdJoinInviteOnly", kingdom.getName());
            return;
        }

        // TODO check for max members

        user.setKingdom(kingdom);
        kdc.save(user);

        kdc.getMessageManager().send(sender, "cmdJoinSuccess", kingdom.getName());

        for ( Player p : kdc.getOnlinePlayers() ) {
            if ( p == sender || kdc.getUser(p).getKingdom() != kingdom ) continue;
            kdc.getMessageManager().send(p, "cmdJoinSuccessMembers", user.getName());
        }

        kdc.getEventManager().kingdomJoin(user);

        // TODO teleport to spawn
		/*
		if ( kingdom.getSpawn() != null && plugin.getCfg().getBoolean("spawn-on-kingdom-join") ) {
			p.teleport(kingdom.getSpawn());
		}
		*/
    }
}
