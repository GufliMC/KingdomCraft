package com.igufguf.kingdomcraft.commands.admin;

import com.igufguf.kingdomcraft.KingdomCraft;
import com.igufguf.kingdomcraft.domain.Kingdom;
import com.igufguf.kingdomcraft.domain.Player;
import com.igufguf.kingdomcraft.models.CommandBase;
import org.bukkit.Bukkit;
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
public class SetKingdomCommand extends CommandBase {

	public SetKingdomCommand(KingdomCraft kingdomCraft) {
		super(kingdomCraft, "setkingdom");
	}
	
	@Override
	public List<String> autocomplete(org.bukkit.entity.Player sender, String[] args) {
		if ( !sender.hasPermission("kingdom.setkingodm") ) {
			return null;
		}

		if ( args.length <= 1 ) {
			return kingdomCraft.playerHandler.getOnlinePlayers().stream()
					.map(Player::getName)
					.collect(Collectors.toList());
		}

		return kingdomCraft.kingdomHandler.getKingdoms().stream().map(Kingdom::getName).collect(Collectors.toList());
	}
	
	@Override
	public void execute(CommandSender sender, String[] args) {
		if ( args.length != 2 ) {
			sendInvalidUsage(sender);
			return;
		}

		Player target = kingdomCraft.playerHandler.getPlayer(args[0]);
		if ( target == null ) {
			kingdomCraft.messageHandler.send(sender, "cmdDefaultNoPlayer");
			return;
		}

		Kingdom kingdom = kingdomCraft.kingdomHandler.getKingdom(args[1]);
		if ( kingdom == null ) {
			kingdomCraft.messageHandler.send(sender, "cmdDefaultKingdomNotExist", args[0]);
			return;
		}

		kingdomCraft.kingdomHandler.quit(target);
		kingdomCraft.kingdomHandler.join(target, kingdom);

		kingdomCraft.kingdomHandler.quit(target);
		kingdomCraft.messageHandler.send(target, "cmdSetKingdomTarget", kingdom.getName());
		kingdomCraft.messageHandler.send(sender, "cmdSetKingdomSender", target.getName(), kingdom.getName());
	}

}
