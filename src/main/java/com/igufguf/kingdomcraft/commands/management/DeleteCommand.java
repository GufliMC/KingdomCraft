package com.igufguf.kingdomcraft.commands.management;

import com.igufguf.kingdomcraft.KingdomCraft;
import com.igufguf.kingdomcraft.domain.Kingdom;
import com.igufguf.kingdomcraft.domain.Player;
import com.igufguf.kingdomcraft.models.CommandBase;
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
public class DeleteCommand extends CommandBase {

	public DeleteCommand(KingdomCraft kingdomCraft) {
		super(kingdomCraft, "delete");
	}
	
	@Override
	public void execute(CommandSender sender, String[] args) {
		if ( args.length != 1 ) {
			sendInvalidUsage(sender);
			return;
		}

		Kingdom target = kingdomCraft.kingdomHandler.getKingdom(args[0]);
		if ( target == null ) {
			kingdomCraft.messageHandler.send(sender, "cmdDefaultKingdomNotExist", args[0]);
			return;
		}

		Player player = kingdomCraft.playerHandler.getPlayer((org.bukkit.entity.Player) sender);

		// other kingdom
		if ( player.getKingdom() != target && !sender.hasPermission("kingdom.delete.other") ) {
			kingdomCraft.messageHandler.send(sender, "noPermission");
			return;
		}

		// own kingdom
		if ( player.getKingdom() == target && !sender.hasPermission("kingdom.delete") ) {
			kingdomCraft.messageHandler.send(sender, "noPermission");
			return;
		}

		kingdomCraft.kingdomHandler.delete(target);
		kingdomCraft.messageHandler.send(sender, "cmdDeleteSuccess", target.getName());
	}

}
