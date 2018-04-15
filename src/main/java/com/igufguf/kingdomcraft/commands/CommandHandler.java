package com.igufguf.kingdomcraft.commands;

import com.igufguf.kingdomcraft.KingdomCraft;
import com.igufguf.kingdomcraft.KingdomCraftMessages;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
public class CommandHandler implements CommandExecutor, TabCompleter {

	private static ArrayList<CommandBase> commands = new ArrayList<>();
	
	public static void register(CommandBase base) {
		commands.add(0, base);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		boolean player = sender instanceof Player;
		if ( player && !KingdomCraft.getApi().enabledWorld(((Player) sender).getWorld()) ) {
			sender.sendMessage(KingdomCraft.prefix + KingdomCraft.getMsg().getMessage("noWorld"));
			return false;
		}
		
		if ( args.length >= 1 ) {

			for ( CommandBase cb : commands ) {
				if ( args[0].equalsIgnoreCase(cb.cmd) || cb.hasAlias(args[0]) ) {
					if ( !player && cb.playeronly ) {
						sender.sendMessage(KingdomCraft.prefix + ChatColor.RED + "This command can only be executed by players!");
						return true;
					} else {
						if ( cb.permission != null && player && !sender.hasPermission(cb.permission)) {
							KingdomCraft.getMsg().send(sender, "noPermissionCmd");
							return true;
						} else {
							ArrayList<String> argslist = new ArrayList<>(Arrays.asList(args));
							argslist.remove(0);
							return cb.execute(sender, argslist.toArray(new String[argslist.size()]));
						}
					}
				}
			}

		} else {
			String prefix = ChatColor.RED + ChatColor.BOLD.toString() + "KingdomCraft" + ChatColor.DARK_GRAY + ChatColor.BOLD + " > " + ChatColor.GRAY;
			sender.sendMessage(prefix + "v" + KingdomCraft.getPlugin(KingdomCraft.class).getDescription().getVersion() + " | Created by iGufGuf");
			sender.sendMessage(prefix + "http://www.igufguf.com");
			sender.sendMessage(prefix + ChatColor.GREEN + "For help type /k help");
			return true;
		}

		KingdomCraft.getMsg().send(sender, "noCommand", args[0]);
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if ( !KingdomCraft.getApi().enabledWorld(((Player) sender).getWorld()) ) {
			return null;
		}
		List<CommandBase> cmds = new ArrayList<>(commands);
		Collections.reverse(cmds);

		if ( args.length >= 1 ) {
			for ( CommandBase cb : cmds ) {
				if ( args[0].equalsIgnoreCase(cb.cmd) || cb.hasAlias(args[0]) ) {
					ArrayList<String> argslist = new ArrayList<>(Arrays.asList(args));
					return cb.tabcomplete(argslist.toArray(new String[argslist.size()]));
				}
			}
		} 
		if ( args.length == 1 ) {
			ArrayList<String> list = new ArrayList<>();
			for ( CommandBase cb : cmds ) {
				list.add(cb.cmd);
			}
			for ( String c : new ArrayList<>(list) ) {
				if ( !c.toLowerCase().startsWith(args[0].toLowerCase()) ) list.remove(c);
			}
			return list;
		}
		return null;
	}
}
