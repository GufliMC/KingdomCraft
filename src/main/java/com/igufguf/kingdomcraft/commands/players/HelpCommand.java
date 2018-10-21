package com.igufguf.kingdomcraft.commands.players;

import com.igufguf.kingdomcraft.KingdomCraft;
import com.igufguf.kingdomcraft.api.models.commands.CommandBase;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

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

	private String cmdPrefix;

	public HelpCommand(KingdomCraft plugin) {
		super("help", null, false);

		this.plugin = plugin;
		this.cmdPrefix = plugin.getMsg().getMessage("cmdHelpCommandPrefix");
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

		return true;
	}

	private void header(CommandSender sender) {
		sender.sendMessage(plugin.getMsg().getMessage("cmdHelpHeader"));
		sender.sendMessage(" ");
	}

	private void footer(CommandSender sender, int page) {
		sender.sendMessage(" ");
		sender.sendMessage(plugin.getMsg().getMessage("cmdHelpFooter", "" + page, "5"));
	}

	private void page1(CommandSender sender) {
		header(sender);
		sender.sendMessage(cmdPrefix + "/k join <kingdom> " + ChatColor.WHITE + plugin.getMsg().getMessage("cmdHelpJoin"));
		sender.sendMessage(cmdPrefix + "/k list "  + ChatColor.WHITE + plugin.getMsg().getMessage("cmdHelpList"));
		sender.sendMessage(cmdPrefix + "/k spawn " + ChatColor.WHITE + plugin.getMsg().getMessage("cmdHelpSpawn"));
		sender.sendMessage(cmdPrefix + "/k info " + ChatColor.WHITE + plugin.getMsg().getMessage("cmdHelpInfo"));
		footer(sender, 1);
	}

	private void page2(CommandSender sender) {
		header(sender);
		sender.sendMessage(cmdPrefix + "/k leave " + ChatColor.WHITE + plugin.getMsg().getMessage("cmdHelpLeave"));
		sender.sendMessage(cmdPrefix + "/k channel <channel> " + ChatColor.WHITE + plugin.getMsg().getMessage("cmdHelpChannel"));
		sender.sendMessage(cmdPrefix + "/k info <kingdom> " + ChatColor.WHITE + plugin.getMsg().getMessage("cmdHelpInfo"));
		sender.sendMessage(cmdPrefix + "/k invite <player> " + ChatColor.WHITE + plugin.getMsg().getMessage("cmdHelpInvite"));
		footer(sender, 2);
	}

	private void page3(CommandSender sender) {
		header(sender);
		sender.sendMessage(cmdPrefix + "/k enemy <kingdom> " + ChatColor.WHITE + plugin.getMsg().getMessage("cmdHelpEnemy"));
		sender.sendMessage(cmdPrefix + "/k friendly <kingdom> " + ChatColor.WHITE + plugin.getMsg().getMessage("cmdHelpFriendly"));
		sender.sendMessage(cmdPrefix + "/k neutral <kingdom> " + ChatColor.WHITE + plugin.getMsg().getMessage("cmdHelpNeutral"));
		sender.sendMessage(cmdPrefix + "/k setspawn <kingdom> " + ChatColor.WHITE + plugin.getMsg().getMessage("cmdHelpSetSpawn"));
		footer(sender, 3);
	}

	private void page4(CommandSender sender) {
		header(sender);
		sender.sendMessage(cmdPrefix + "/k setrank <player> <rank> " + ChatColor.WHITE + plugin.getMsg().getMessage("cmdHelpSetRank"));
		sender.sendMessage(cmdPrefix + "/k kick <player> " + ChatColor.WHITE + plugin.getMsg().getMessage("cmdHelpKick"));
		sender.sendMessage(cmdPrefix + "/k spawn <kingdom> " + ChatColor.WHITE + plugin.getMsg().getMessage("cmdHelpSpawn"));
		sender.sendMessage(cmdPrefix + "/k set <player> <kingdom> " + ChatColor.WHITE + plugin.getMsg().getMessage("cmdHelpSet"));
		footer(sender, 4);
	}

	private void page5(CommandSender sender) {
		header(sender);
		sender.sendMessage(cmdPrefix + "/k flag <kingdom> <flag> <value> " + ChatColor.WHITE + plugin.getMsg().getMessage("cmdHelpFlagSet"));
		sender.sendMessage(cmdPrefix + "/k flag list <kingdom> " + ChatColor.WHITE + plugin.getMsg().getMessage("cmdHelpFlagList"));
		sender.sendMessage(cmdPrefix + "/k socialspy " + ChatColor.WHITE + plugin.getMsg().getMessage("cmdHelpSocialSpy"));
		sender.sendMessage(cmdPrefix + "/k reload " + ChatColor.WHITE + plugin.getMsg().getMessage("cmdHelpReload"));
		footer(sender, 5);
	}
}
