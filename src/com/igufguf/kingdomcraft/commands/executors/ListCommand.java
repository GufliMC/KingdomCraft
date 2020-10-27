package com.igufguf.kingdomcraft.commands.executors;

import com.igufguf.kingdomcraft.commands.CommandBase;
import com.igufguf.kingdomcraft.commands.CommandHandler;
import com.igufguf.kingdomcraft.KingdomCraft;
import com.igufguf.kingdomcraft.objects.KingdomObject;
import com.igufguf.kingdomcraft.KingdomCraftMessages;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

/**
 * Copyrighted 2017 iGufGuf
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

	public ListCommand() {
		super("list", null, false);
		addAliasses("l");
		
		CommandHandler.register(this);
	}
	
	@Override
	public ArrayList<String> tabcomplete(String[] args) {
		return null;
	}
	
	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if ( args.length != 0 ) {
			KingdomCraft.getMsg().send(sender, "cmdDefaultUsage");
			return false;
		}
		
		ArrayList<String> closed = new ArrayList<String>();
		ArrayList<String> open = new ArrayList<String>();
		for ( KingdomObject kd : KingdomCraft.getApi().getKingdoms() ) {
			if ( kd.hasInList("flags", "closed") ) {
				closed.add(kd.getName());
			} else {
				open.add(kd.getName());
			}
		}
		String s = ChatColor.GREEN + "";
		for ( String ope : open ) {
			s = s + ChatColor.GRAY + ", " + ChatColor.GREEN + ope;
		}
		for ( String close : closed ) {
			s = s + ChatColor.GRAY + ", " + ChatColor.RED + close;
		}
		s = s.replaceFirst(", ", "");

		sender.sendMessage(KingdomCraft.prefix + KingdomCraft.getMsg().getMessage("cmdList") + " " + s);

		return false;
	}

}
