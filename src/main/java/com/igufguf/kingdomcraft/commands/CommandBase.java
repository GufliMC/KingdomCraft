package com.igufguf.kingdomcraft.commands;

import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
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
public abstract class CommandBase {

	public String permission;
	public boolean playeronly;
	public String cmd;
	public List<String> aliases = new ArrayList<String>();
	
	public CommandBase(String cmd, String permission, boolean playeronly) {
		this.cmd = cmd;
		this.permission = permission;
		this.playeronly = playeronly;
	}
	
	public void addAliasses(String... aliases) {
		this.aliases.addAll(Arrays.asList(aliases));
	}
	
	public boolean hasAlias(String alias) {
		return aliases.contains(alias);
	}
	
	public abstract boolean execute(CommandSender sender, String[] args);
	
	public abstract ArrayList<String> tabcomplete(String[] args);
	
}
