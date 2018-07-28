package com.igufguf.kingdomcraft.commands.executors.players;

import com.igufguf.kingdomcraft.KingdomCraft;
import com.igufguf.kingdomcraft.commands.CommandBase;
import com.igufguf.kingdomcraft.commands.CommandHandler;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyrighted 2018 iGufGuf
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

	private final KingdomCraft plugin;

	public HelpCommand(KingdomCraft plugin) {
		super("help", null, false);

		this.plugin = plugin;

		plugin.getCmdHandler().register(this);
	}
	
	@Override
	public List<String> tabcomplete(CommandSender sender, String[] args) {
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

	private void header(CommandSender sender) {
		sender.sendMessage(ChatColor.YELLOW + "--------------- " + ChatColor.GOLD + "Kingdom " + ChatColor.YELLOW + "---------------");
		sender.sendMessage(ChatColor.GREEN + "     Aliasses: kingdom - kingdoms - k");
		sender.sendMessage(" ");
	}

	private void footer(CommandSender sender, int page) {
		sender.sendMessage(" ");
		sender.sendMessage(ChatColor.YELLOW + "Page " + page + "/5");
	}

	private void page1(CommandSender sender) {
		header(sender);
		sender.sendMessage(ChatColor.YELLOW + "/k join <kingdom> " + ChatColor.WHITE + plugin.getMsg().getMessage("cmdHelpJoin"));
		sender.sendMessage(ChatColor.YELLOW + "/k list "  + ChatColor.WHITE + plugin.getMsg().getMessage("cmdHelpList"));
		sender.sendMessage(ChatColor.YELLOW + "/k spawn " + ChatColor.WHITE + plugin.getMsg().getMessage("cmdHelpSpawn"));
		sender.sendMessage(ChatColor.YELLOW + "/k info " + ChatColor.WHITE + plugin.getMsg().getMessage("cmdHelpInfo"));
		footer(sender, 1);
	}

	private void page2(CommandSender sender) {
		header(sender);
		sender.sendMessage(ChatColor.YELLOW + "/k leave " + ChatColor.WHITE + plugin.getMsg().getMessage("cmdHelpLeave"));
		sender.sendMessage(ChatColor.YELLOW + "/k channel <channel> " + ChatColor.WHITE + plugin.getMsg().getMessage("cmdHelpChannel"));
		sender.sendMessage(ChatColor.YELLOW + "/k info <kingdom> " + ChatColor.WHITE + plugin.getMsg().getMessage("cmdHelpInfo"));
		sender.sendMessage(ChatColor.YELLOW + "/k spawn <kingdom> " + ChatColor.WHITE + plugin.getMsg().getMessage("cmdHelpSpawn"));
		footer(sender, 2);
	}

	private void page3(CommandSender sender) {
		header(sender);
		sender.sendMessage(ChatColor.YELLOW + "/k invite <player> " + ChatColor.WHITE + plugin.getMsg().getMessage("cmdHelpInvite"));
		sender.sendMessage(ChatColor.YELLOW + "/k enemy <kingdom> " + ChatColor.WHITE + plugin.getMsg().getMessage("cmdHelpEnemy"));
		sender.sendMessage(ChatColor.YELLOW + "/k friendly <kingdom> " + ChatColor.WHITE + plugin.getMsg().getMessage("cmdHelpFriendly"));
		sender.sendMessage(ChatColor.YELLOW + "/k neutral <kingdom> " + ChatColor.WHITE + plugin.getMsg().getMessage("cmdHelpNeutral"));
		footer(sender, 3);
	}

	private void page4(CommandSender sender) {
		header(sender);
		sender.sendMessage(ChatColor.YELLOW + "/k setspawn <kingdom> " + ChatColor.WHITE + plugin.getMsg().getMessage("cmdHelpSetSpawn"));
		sender.sendMessage(ChatColor.YELLOW + "/k setrank <player> <rank> " + ChatColor.WHITE + plugin.getMsg().getMessage("cmdHelpSetRank"));
		sender.sendMessage(ChatColor.YELLOW + "/k kick <player> " + ChatColor.WHITE + plugin.getMsg().getMessage("cmdHelpKick"));
		sender.sendMessage(ChatColor.YELLOW + "/k set <player> <kingdom> " + ChatColor.WHITE + plugin.getMsg().getMessage("cmdHelpSet"));
		footer(sender, 4);
	}

	private void page5(CommandSender sender) {
		header(sender);
		sender.sendMessage(ChatColor.YELLOW + "/k flag <kingdom> <flag> <value> " + ChatColor.WHITE + plugin.getMsg().getMessage("cmdHelpFlagSet"));
		sender.sendMessage(ChatColor.YELLOW + "/k flag list <kingdom>" + ChatColor.WHITE + plugin.getMsg().getMessage("cmdHelpFlagList"));
		sender.sendMessage(ChatColor.YELLOW + "/k reload " + ChatColor.WHITE + plugin.getMsg().getMessage("cmdHelpReload"));
		footer(sender, 5);
	}
}
