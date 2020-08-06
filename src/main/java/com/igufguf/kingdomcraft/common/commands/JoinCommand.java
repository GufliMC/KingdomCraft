package com.igufguf.kingdomcraft.common.commands;

import com.igufguf.kingdomcraft.bukkit.KingdomCraft;
import com.igufguf.kingdomcraft.common.domain.DKingdom;
import com.igufguf.kingdomcraft.common.domain.DKingdomInvite;
import com.igufguf.kingdomcraft.common.domain.DPlayer;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Copyrighted 2020 iGufGuf
 *
 * This file is part of KingdomCraft.
 *
 * Kingdomcraft is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * KingdomCraft is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with KingdomCraft.  If not, see <http://www.gnu.org/licenses/>.
 *
 **/
public class JoinCommand extends CommandBase {

	public JoinCommand(KingdomCraft kingdomCraft) {
		super(kingdomCraft, "join");
	}
	
	@Override
	public List<String> autocomplete(org.bukkit.entity.Player sender, String[] args) {
		if ( args.length != 1 ) {
			return null;
		}

		return kingdomCraft.kingdomHandler.getKingdoms().stream().map(DKingdom::getName).collect(Collectors.toList());
	}
	
	@Override
	public void execute(CommandSender sender, String[] args) {
		if ( args.length != 1 ) {
			sendInvalidUsage(sender);
			return;
		}

		DKingdom target = kingdomCraft.kingdomHandler.getKingdom(args[0]);
		if ( target == null ) {
			kingdomCraft.messageHandler.send(sender, "cmdDefaultKingdomNotExist", args[0]);
			return;
		}

		DPlayer player = kingdomCraft.playerHandler.getPlayer((org.bukkit.entity.Player) sender);
		if ( player.getKingdom() != null ) {
			kingdomCraft.messageHandler.send(sender, "cmdJoinAlready");
			return;
		}

		if ( target.isInviteOnly() ) {
			kingdomCraft.messageHandler.send(sender, "cmdJoinInviteOnly", target.getName());

			DKingdomInvite invite = kingdomCraft.kingdomHandler.getInvite(player, target);
			if ( invite == null ) {
				kingdomCraft.messageHandler.send(sender, "cmdJoinInviteOnly", target.getName());
				return;
			}

			return;
		}

		// TODO check for max members

		for ( DPlayer member : kingdomCraft.kingdomHandler.getOnlineMembers(target) ) {
			kingdomCraft.messageHandler.send(member, "cmdJoinSuccessMembers", player.getName());
		}

		kingdomCraft.kingdomHandler.join(player, target);
		kingdomCraft.messageHandler.send(sender, "cmdJoinSuccess", target.getName());

		// TODO teleport to spawn
		/*
		if ( kingdom.getSpawn() != null && plugin.getCfg().getBoolean("spawn-on-kingdom-join") ) {
			p.teleport(kingdom.getSpawn());
		}
		*/
	}

}
