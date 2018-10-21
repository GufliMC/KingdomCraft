package com.igufguf.kingdomcraft.handlers;

import com.igufguf.kingdomcraft.KingdomCraft;
import com.igufguf.kingdomcraft.api.handlers.KingdomCommandHandler;
import com.igufguf.kingdomcraft.api.models.commands.CommandBase;
import org.apache.commons.text.similarity.LevenshteinDistance;
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
public class SimpleCommandHandler implements CommandExecutor, TabCompleter, KingdomCommandHandler {

	private final KingdomCraft plugin;
	private final List<CommandBase> commands = new ArrayList<>();
	
	public SimpleCommandHandler(KingdomCraft plugin) {
		this.plugin = plugin;
	}

	// API

	@Override
	public void register(CommandBase base) {
		commands.add(0, base);
	}

	@Override

	public void unregister(CommandBase base) {
		commands.remove(base);
	}

	@Override
	public CommandBase getByCommand(String cmd) {
		for ( CommandBase cb : commands ) {
			if ( cb.getCommand().equalsIgnoreCase(cmd) ) return cb;
		}
		return null;
	}

	// HANDLING

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		boolean player = sender instanceof Player;
		if ( player && !plugin.getApi().isWorldEnabled(((Player) sender).getWorld()) ) {
			plugin.getMsg().send(sender, "noWorld");
			return true;
		}
		
		if ( args.length >= 1 ) {

			for ( CommandBase cb : commands ) {
				if ( args[0].equalsIgnoreCase(cb.getCommand()) || cb.hasAlias(args[0]) ) {
					if ( !player && cb.isPlayerOnly() ) {
						sender.sendMessage(plugin.getPrefix() + ChatColor.RED + "This command can only be executed by players!");
						return true;
					} else {
						if ( cb.getPermission() != null && player && !sender.hasPermission(cb.getPermission())) {
							plugin.getMsg().send(sender, "noPermissionCmd");
							return true;
						} else {
							boolean correct = cb.execute(sender, Arrays.copyOfRange(args, 1, args.length));

							if ( !correct ) {
								if ( cb.getUsage() == null ) {
									plugin.getMsg().send(sender, "cmdDefaultUsage");
								} else {
									plugin.getMsg().send(sender, "cmdDefaultUsageExplain",
											"/" + label.toLowerCase() + " " + cb.getCommand() + " " + cb.getUsage());
								}
							}

							return true;
						}
					}
				}
			}

		} else {
			String prefix = ChatColor.RED + ChatColor.BOLD.toString() + "KingdomCraft" + ChatColor.DARK_GRAY + ChatColor.BOLD + " > " + ChatColor.GRAY;
			sender.sendMessage(prefix + "v" + plugin.getDescription().getVersion() + " | Created by iGufGuf");
			sender.sendMessage(prefix + "https://www.igufguf.com");
			sender.sendMessage(prefix + ChatColor.GREEN + "For help type /k help");
			return true;
		}

		// suggest a command if the command doesn't exist
		LevenshteinDistance ld = new LevenshteinDistance(5);
		int lowestScore = Integer.MAX_VALUE;
		CommandBase lowestCommand = null;
		for ( CommandBase cb : commands ) {
			int score = ld.apply(args[0], cb.getCommand());
			if ( score != -1 && score < lowestScore ) {
				lowestScore = score;
				lowestCommand = cb;

				if ( score == 1 ) break;
			}
		}

		if ( lowestCommand != null ) {
			plugin.getMsg().send(sender, "cmdDefaultHint",
					"/" + label.toLowerCase() + " " + lowestCommand.getCommand()
							+ (lowestCommand.getUsage() != null ? " " + lowestCommand.getUsage() : ""));
			return true;
		}

		plugin.getMsg().send(sender, "noCommand", args[0]);
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if ( !plugin.getApi().isWorldEnabled(((Player) sender).getWorld()) ) {
			return null;
		}
		List<CommandBase> cmds = new ArrayList<>(commands);
		Collections.reverse(cmds);

		if ( args.length >= 1 ) {
			for ( CommandBase cb : cmds ) {
				if ( args[0].equalsIgnoreCase(cb.getCommand()) || cb.hasAlias(args[0]) ) {
					return cb.tabcomplete(sender, Arrays.copyOfRange(args, 1, args.length));
				}
			}
		}

		// autocomplete commands
		if ( args.length == 1 ) {
			ArrayList<String> list = new ArrayList<>();
			for ( CommandBase cb : cmds ) {
				list.add(cb.getCommand());
			}
			for ( String c : new ArrayList<>(list) ) {
				if ( !c.toLowerCase().startsWith(args[0].toLowerCase()) ) list.remove(c);
			}
			return list;
		}
		return null;
	}
}
