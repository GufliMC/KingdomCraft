package com.igufguf.kingdomcraft.models;

import com.igufguf.kingdomcraft.KingdomCraft;
import com.igufguf.kingdomcraft.domain.Player;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
public abstract class CommandBase {

	protected final KingdomCraft kingdomCraft;

	private List<String> commandAliasses = new ArrayList<String>();

	private CommandBase parent;
	private List<CommandBase> children = new ArrayList<>();

	private String hint;

	public CommandBase(KingdomCraft kingdomCraft, String... commandAliases) {
		this.kingdomCraft = kingdomCraft;
		this.commandAliasses = Arrays.asList(commandAliases);
	}

	public CommandBase(CommandBase parent, String... commandAliases) {
		this.kingdomCraft = parent.kingdomCraft;
		this.parent = parent;
		this.commandAliasses = Arrays.asList(commandAliases);
	}

	public List<String> getCommandAliasses() {
		return commandAliasses;
	}
	
	public boolean hasCommandAlias(String alias) {
		return commandAliasses.contains(alias);
	}

	public List<CommandBase> getChildren() {
		return children;
	}

	public void addChild(CommandBase cb) {
		children.add(cb);
	}

	public String getHint() {
		if ( parent == null ) {
			return hint;
		}
		return parent.getHint() + " " + hint;
	}

	public void setHint(String hint) {
		this.hint = hint;
	}

	// run chain

	public void executeChain(CommandSender sender, String[] args) {
		if ( args.length > 0 && !children.isEmpty() ) {
			for (CommandBase child : children) {
				if (child.getCommandAliasses().stream().anyMatch(ca -> ca.equalsIgnoreCase(args[0]))) {
					child.executeChain(sender, Arrays.copyOfRange(args, 1, args.length));
					return;
				}
			}
		}
		execute(sender, args);
	}

	public List<String> autocompleteChain(org.bukkit.entity.Player sender, String[] args) {
		if ( args.length > 0 && !children.isEmpty() ) {
			for (CommandBase child : children) {
				if (child.getCommandAliasses().stream().anyMatch(ca -> ca.equalsIgnoreCase(args[0]))) {
					return autocompleteChain(sender, Arrays.copyOfRange(args, 1, args.length));
				}
			}
		}

		List<String> result = autocompleteChain(sender, args);
		if ( result == null ) {
			return null;
		}

		if ( args.length > 0 ) {
			return result.stream().filter(s -> s.toLowerCase().startsWith(args[args.length - 1].toLowerCase())).collect(Collectors.toList());
		}

		return result;
	}

	// logic to be overriden

	public abstract void execute(CommandSender sender, String[] args);
	
	public List<String> autocomplete(org.bukkit.entity.Player sender, String[] args) {
		return null;
	}

	// helpers

	protected void sendInvalidUsage(CommandSender sender) {
		CommandBase parent = this.parent;
		String fullCommand = "";
		while ( parent != null ) {
			fullCommand = parent.getCommandAliasses().get(0) + " " + fullCommand;
			parent = parent.parent;
		}

		kingdomCraft.messageHandler.send(sender, "cmdDefaultUsage", "/k " + fullCommand + " " + this.hint);
	}

}
