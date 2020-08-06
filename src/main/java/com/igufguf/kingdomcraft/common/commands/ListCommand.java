package com.igufguf.kingdomcraft.common.commands;

import com.igufguf.kingdomcraft.bukkit.KingdomCraft;
import com.igufguf.kingdomcraft.common.domain.DKingdom;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Comparator;
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
public class ListCommand extends CommandBase {

	public ListCommand(KingdomCraft kingdomCraft) {
		super(kingdomCraft, "list");
	}
	
	@Override
	public void execute(CommandSender sender, String[] args) {
		if ( args.length != 0 ) {
			sendInvalidUsage(sender);
			return;
		}

		List<String> kingdoms = kingdomCraft.kingdomHandler.getKingdoms().stream()
				.sorted(Comparator.comparing(DKingdom::isInviteOnly))
				.map(k -> (k.isInviteOnly() ? ChatColor.RED : ChatColor.GREEN) + k.getName())
				.collect(Collectors.toList());

		sender.sendMessage(kingdomCraft.messageHandler + kingdomCraft.messageHandler.getMessage("cmdList") + " "
				+ String.join(ChatColor.GRAY + ", ", kingdoms));
	}

}
