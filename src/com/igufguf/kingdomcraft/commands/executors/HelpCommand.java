package com.igufguf.kingdomcraft.commands.executors;

import com.igufguf.kingdomcraft.commands.CommandBase;
import com.igufguf.kingdomcraft.commands.CommandHandler;
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
public class HelpCommand extends CommandBase {

	public HelpCommand() {
		super("help", null, false);
		
		CommandHandler.register(this);
	}
	
	@Override
	public ArrayList<String> tabcomplete(String[] args) {
		return null;
	}
	
	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if ( args.length == 1 && args[0].matches("[0-9]+") ) {
			if ( args[0].equalsIgnoreCase("1") ) page1(sender);
			else if ( args[0].equalsIgnoreCase("2") ) page2(sender);
			else if ( args[0].equalsIgnoreCase("3") ) page3(sender);
			else if ( args[0].equalsIgnoreCase("4") ) page4(sender);
			else if ( args[0].equalsIgnoreCase("5") ) page5(sender);
		} else {
			page1(sender);
		}
		
		return false;
	}
	
	public static void page1(CommandSender sender) {
		sender.sendMessage(ChatColor.YELLOW + "--------------- §6Kingdom §e---------------");
		sender.sendMessage(ChatColor.GREEN + "     Aliasses: kingdom - kingdoms - k");
		sender.sendMessage(" ");
		sender.sendMessage(ChatColor.YELLOW + "/k join <kingdom> §fJoin a kingdom");
		sender.sendMessage(ChatColor.YELLOW + "/k list §fGet a list of all kingdoms");
		sender.sendMessage(ChatColor.YELLOW + "/k spawn §fTeleport to the kingdom spawn");
		sender.sendMessage(ChatColor.YELLOW + "/k info §fGet your kingdom information");
		sender.sendMessage(" ");
		sender.sendMessage(ChatColor.YELLOW + "Page 1/5");
	}
	
	public static void page2(CommandSender sender) {
		sender.sendMessage(ChatColor.YELLOW + "--------------- §6Kingdom §e---------------");
		sender.sendMessage(ChatColor.GREEN + "     Aliasses: kingdom - kingdoms - k");
		sender.sendMessage(" ");
		sender.sendMessage(ChatColor.YELLOW + "/k leave §fLeave your kingdom");
		sender.sendMessage(ChatColor.YELLOW + "/k channel <channel> §fToggle a channel to see and talk in!");
		sender.sendMessage(ChatColor.YELLOW + "/k info <kingdom> §fSee the info of a kingdom or a player");
		sender.sendMessage(ChatColor.YELLOW + "/k setrank <player> <rank> §fChange a players rank");
		sender.sendMessage(" ");
		sender.sendMessage(ChatColor.YELLOW + "Page 2/5");
	}
	
	public static void page3(CommandSender sender) {
		sender.sendMessage(ChatColor.YELLOW + "--------------- §6Kingdom §e---------------");
		sender.sendMessage(ChatColor.GREEN + "     Aliasses: kingdom - kingdoms - k");
		sender.sendMessage(" ");
		sender.sendMessage(ChatColor.YELLOW + "/k enemy <kingdom> §fDeclare war with a kingdom");
		sender.sendMessage(ChatColor.YELLOW + "/k friendly <kingdom> §fSet a kingdom to friendly");
		sender.sendMessage(ChatColor.YELLOW + "/k neutral <kingdom> §fDisband an alliance");
		sender.sendMessage(ChatColor.YELLOW + "/k setspawn <kingdom> §fSet your kingdom spawn");
		sender.sendMessage(" ");
		sender.sendMessage(ChatColor.YELLOW + "Page 3/5");
	}
	
	public static void page4(CommandSender sender) {
		sender.sendMessage(ChatColor.YELLOW + "--------------- §6Kingdom §e---------------");
		sender.sendMessage(ChatColor.GREEN + "     Aliasses: kingdom - kingdoms - k");
		sender.sendMessage(" ");
		sender.sendMessage(ChatColor.YELLOW + "/k settype [open/closed] §fSet your kingdom to private or public");
		sender.sendMessage(ChatColor.YELLOW + "/k spawn <kingdom> §fTeleport to another kingdoms spawn");
		sender.sendMessage(ChatColor.YELLOW + "/k kick <player> §fKick a player out of his kingdom");
		sender.sendMessage(ChatColor.YELLOW + "/k set <player> <kingdom> §fMove a player to a kingdom");
		sender.sendMessage(" ");
		sender.sendMessage(ChatColor.YELLOW + "Page 4/5");
	}
	
	public static void page5(CommandSender sender) {
		sender.sendMessage(ChatColor.YELLOW + "--------------- §6Kingdom §e---------------");
		sender.sendMessage(ChatColor.GREEN + "     Aliasses: kingdom - kingdoms - k");
		sender.sendMessage(" ");
		sender.sendMessage(ChatColor.YELLOW + "/k region define <kingdom> <region> §fLink a worldguard region to a kingdom");
		sender.sendMessage(ChatColor.YELLOW + "/k flagg add <kingdom> <flag> §fAdd a flag to a kingdom");
		sender.sendMessage(ChatColor.YELLOW + "/k flagg delete <kingdom> <flag> §fRemove a flag from a kingdom");
		sender.sendMessage(" ");
		sender.sendMessage(ChatColor.YELLOW + "Page 5/5");
	}
}
