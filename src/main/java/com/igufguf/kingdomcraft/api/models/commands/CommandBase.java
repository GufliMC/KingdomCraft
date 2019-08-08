package com.igufguf.kingdomcraft.api.models.commands;

import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
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
public abstract class CommandBase {

	private String usage = "";
	private String permission = null;
	private boolean isPlayerOnly = false;

	private String command;
	private List<String> aliases = new ArrayList<String>();

	public CommandBase(String cmd) {
		this.command = cmd;
	}

	public CommandBase(String cmd, String permission, boolean isPlayerOnly) {
		this(cmd);
		this.permission = permission;
		this.isPlayerOnly = isPlayerOnly;
	}

	public CommandBase(String cmd, String permission, boolean isPlayerOnly, String usage) {
		this(cmd, permission, isPlayerOnly);
		this.usage = usage;
	}

	public String getCommand() {
		return command;
	}
	
	public void addAliasses(String... aliases) {
		this.aliases.addAll(Arrays.asList(aliases));
	}
	
	public boolean hasAlias(String alias) {
		return aliases.contains(alias);
	}

	public String getPermission() {
		return permission;
	}

	public boolean isPlayerOnly() {
		return isPlayerOnly;
	}

	public String getUsage() {
		return usage;
	}

	public abstract boolean execute(CommandSender sender, String[] args);
	
	public List<String> tabcomplete(CommandSender sender, String[] args) {
		return null;
	}
	
}
