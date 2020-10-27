package com.igufguf.kingdomcraft.commands.executors;

import com.igufguf.kingdomcraft.KingdomCraft;
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
		} else {
			page1(sender);
		}

		return false;
	}
	
	public static void page1(CommandSender sender) {
		sender.sendMessage(ChatColor.YELLOW + "--------------- " + ChatColor.GOLD + "Kingdom " + ChatColor.YELLOW + "---------------");
		sender.sendMessage(ChatColor.GREEN + "     Aliasses: kingdom - kingdoms - k");
		sender.sendMessage(" ");
		sender.sendMessage(ChatColor.YELLOW + "/k join <kingdom> " + ChatColor.WHITE + KingdomCraft.getMsg().getMessage("cmdHelpJoin"));
		sender.sendMessage(ChatColor.YELLOW + "/k list "  + ChatColor.WHITE + KingdomCraft.getMsg().getMessage("cmdHelpList"));
		sender.sendMessage(ChatColor.YELLOW + "/k spawn " + ChatColor.WHITE + KingdomCraft.getMsg().getMessage("cmdHelpSpawn"));
		sender.sendMessage(ChatColor.YELLOW + "/k info " + ChatColor.WHITE + KingdomCraft.getMsg().getMessage("cmdHelpInfo"));
		sender.sendMessage(" ");
		sender.sendMessage(ChatColor.YELLOW + "Page 1/4");
	}
	
	public static void page2(CommandSender sender) {
		sender.sendMessage(ChatColor.YELLOW + "--------------- " + ChatColor.GOLD + "Kingdom " + ChatColor.YELLOW + "---------------");
		sender.sendMessage(ChatColor.GREEN + "     Aliasses: kingdom - kingdoms - k");
		sender.sendMessage(" ");
		sender.sendMessage(ChatColor.YELLOW + "/k leave " + ChatColor.WHITE + KingdomCraft.getMsg().getMessage("cmdHelpLeave"));
		sender.sendMessage(ChatColor.YELLOW + "/k channel <channel> " + ChatColor.WHITE + KingdomCraft.getMsg().getMessage("cmdHelpChannel"));
		sender.sendMessage(ChatColor.YELLOW + "/k info <kingdom> " + ChatColor.WHITE + KingdomCraft.getMsg().getMessage("cmdHelpInfo"));
		sender.sendMessage(ChatColor.YELLOW + "/k setrank <player> <rank> " + ChatColor.WHITE + KingdomCraft.getMsg().getMessage("cmdHelpSetrank"));
		sender.sendMessage(" ");
		sender.sendMessage(ChatColor.YELLOW + "Page 2/4");
	}
	
	public static void page3(CommandSender sender) {
		sender.sendMessage(ChatColor.YELLOW + "--------------- " + ChatColor.GOLD + "Kingdom " + ChatColor.YELLOW + "---------------");
		sender.sendMessage(ChatColor.GREEN + "     Aliasses: kingdom - kingdoms - k");
		sender.sendMessage(" ");
		sender.sendMessage(ChatColor.YELLOW + "/k enemy <kingdom> " + ChatColor.WHITE + KingdomCraft.getMsg().getMessage("cmdHelpEnemy"));
		sender.sendMessage(ChatColor.YELLOW + "/k friendly <kingdom> " + ChatColor.WHITE + KingdomCraft.getMsg().getMessage("cmdHelpFriendly"));
		sender.sendMessage(ChatColor.YELLOW + "/k neutral <kingdom> " + ChatColor.WHITE + KingdomCraft.getMsg().getMessage("cmdHelpNeutral"));
		sender.sendMessage(ChatColor.YELLOW + "/k setspawn <kingdom> " + ChatColor.WHITE + KingdomCraft.getMsg().getMessage("cmdHelpSetspawn"));
		sender.sendMessage(" ");
		sender.sendMessage(ChatColor.YELLOW + "Page 3/4");
	}
	
	public static void page4(CommandSender sender) {
		sender.sendMessage(ChatColor.YELLOW + "--------------- " + ChatColor.GOLD + "Kingdom " + ChatColor.YELLOW + "---------------");
		sender.sendMessage(ChatColor.GREEN + "     Aliasses: kingdom - kingdoms - k");
		sender.sendMessage(" ");
		sender.sendMessage(ChatColor.YELLOW + "/k setstatus [open/closed] " + ChatColor.WHITE + KingdomCraft.getMsg().getMessage("cmdHelpSetstatus"));
		sender.sendMessage(ChatColor.YELLOW + "/k spawn <kingdom> " + ChatColor.WHITE + KingdomCraft.getMsg().getMessage("cmdHelpSpawn"));
		sender.sendMessage(ChatColor.YELLOW + "/k kick <player> " + ChatColor.WHITE + KingdomCraft.getMsg().getMessage("cmdHelpKick"));
		sender.sendMessage(ChatColor.YELLOW + "/k set <player> <kingdom> " + ChatColor.WHITE + KingdomCraft.getMsg().getMessage("cmdHelpSet"));
		sender.sendMessage(" ");
		sender.sendMessage(ChatColor.YELLOW + "Page 4/4");
	}
}
