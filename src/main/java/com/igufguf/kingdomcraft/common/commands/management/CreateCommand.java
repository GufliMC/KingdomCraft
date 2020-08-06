package com.igufguf.kingdomcraft.common.commands.management;

import com.igufguf.kingdomcraft.bukkit.KingdomCraft;
import com.igufguf.kingdomcraft.common.domain.DKingdom;
import com.igufguf.kingdomcraft.common.domain.DPlayer;
import com.igufguf.kingdomcraft.common.commands.CommandBase;
import org.bukkit.command.CommandSender;

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
public class CreateCommand extends CommandBase {

	public CreateCommand(KingdomCraft kingdomCraft) {
		super(kingdomCraft, "create");
	}
	
	@Override
	public void execute(CommandSender sender, String[] args) {
		if ( args.length != 1 ) {
			sendInvalidUsage(sender);
			return;
		}

		if ( !args[0].matches("[a-zA-Z0-9]+") ) {
			kingdomCraft.messageHandler.send(sender, "cmdCreateNameInvalid", args[0]);
			return;
		}

		if ( kingdomCraft.kingdomHandler.getKingdom(args[0]) != null ) {
			kingdomCraft.messageHandler.send(sender, "cmdCreateAlreadyExists", args[0]);
			return;
		}

		DKingdom kingdom = kingdomCraft.kingdomHandler.create(args[0]);
		kingdomCraft.messageHandler.send(sender, "cmdCreateSuccess", kingdom.getName());

		DPlayer player = kingdomCraft.playerHandler.getPlayer((org.bukkit.entity.Player) sender);
		player.setKingdom(kingdom);

		kingdomCraft.messageHandler.send(sender, "cmdCreateSuccess", kingdom.getName());
	}

}
