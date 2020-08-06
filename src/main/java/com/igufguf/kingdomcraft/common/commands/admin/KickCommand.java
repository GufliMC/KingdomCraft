package com.igufguf.kingdomcraft.common.commands.admin;

import com.igufguf.kingdomcraft.bukkit.KingdomCraft;
import com.igufguf.kingdomcraft.common.domain.DKingdom;
import com.igufguf.kingdomcraft.common.domain.DPlayer;
import com.igufguf.kingdomcraft.common.commands.CommandBase;
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
public class KickCommand extends CommandBase {

	public KickCommand(KingdomCraft kingdomCraft) {
		super(kingdomCraft, "kick");
	}
	
	@Override
	public List<String> autocomplete(org.bukkit.entity.Player sender, String[] args) {
		if ( sender.hasPermission("kingdom.kick.other") ) {
			return kingdomCraft.playerHandler.getOnlinePlayers().stream()
					.filter(p -> p.getId() != sender.getUniqueId())
					.filter(p -> p.getKingdom() != null)
					.map(DPlayer::getName)
					.collect(Collectors.toList());
		}

		DPlayer player = kingdomCraft.playerHandler.getPlayer(sender);
		if ( player.getName() == null || !sender.hasPermission("kingdom.kick") ) {
			return null;
		}

		return kingdomCraft.kingdomHandler.getOnlineMembers(player.getKingdom()).stream()
				.filter(p -> p != player)
				.map(DPlayer::getName)
				.collect(Collectors.toList());
	}
	
	@Override
	public void execute(CommandSender sender, String[] args) {
		if ( args.length != 1 ) {
			sendInvalidUsage(sender);
			return;
		}

		DPlayer player = kingdomCraft.playerHandler.getPlayer((org.bukkit.entity.Player) sender);

		DPlayer target = kingdomCraft.playerHandler.getPlayer(args[0]);
		if ( target == null ) {
			kingdomCraft.messageHandler.send(sender, "cmdDefaultNoPlayer");
			return;
		}
		DKingdom kingdom = target.getKingdom();

		// kick other kingdom
		if ( kingdom != player.getKingdom() && !sender.hasPermission("kingdom.kick.other") ) {
			kingdomCraft.messageHandler.send(sender, "noPermissionCmd");
			return;
		}

		// kick own kingdom
		if ( kingdom == player.getKingdom() && !sender.hasPermission("kingdom.kick") ) {
			kingdomCraft.messageHandler.send(sender, "noPermissionCmd");
			return;
		}


		kingdomCraft.kingdomHandler.quit(target);
		kingdomCraft.messageHandler.send(target, "cmdKickTarget", kingdom.getName());
		kingdomCraft.messageHandler.send(sender, "cmdKickSender", target.getName(), kingdom.getName());
	}

}
