package com.igufguf.kingdomcraft.commands.players;

import com.igufguf.kingdomcraft.api.models.commands.CommandBase;
import com.igufguf.kingdomcraft.KingdomCraft;
import com.igufguf.kingdomcraft.api.models.flags.KingdomFlag;
import com.igufguf.kingdomcraft.handlers.SimpleFlagHandler;
import com.igufguf.kingdomcraft.api.models.kingdom.Kingdom;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

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
public class ListCommand extends CommandBase {

	private final KingdomCraft plugin;

	public ListCommand(KingdomCraft plugin) {
		super("list", null, false);
		addAliasses("l");

		this.plugin = plugin;
	}
	
	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if ( args.length != 0 ) {
			plugin.getMsg().send(sender, "cmdDefaultUsage");
			return false;
		}
		
		ArrayList<String> inviteOnly = new ArrayList<String>();
		ArrayList<String> open = new ArrayList<String>();

		for ( Kingdom kd : plugin.getApi().getKingdomHandler().getKingdoms() ) {
			if ( plugin.getApi().getFlagHandler().hasFlag(kd, KingdomFlag.INVITE_ONLY)
					&& plugin.getApi().getFlagHandler().getFlagValue(kd, KingdomFlag.INVITE_ONLY) ) {
				inviteOnly.add(kd.getName());
			} else {
				open.add(kd.getName());
			}
		}

		String s = ChatColor.GREEN + "";
		for ( String ope : open ) {
			s = s + ChatColor.GRAY + ", " + ChatColor.GREEN + ope;
		}

		for ( String close : inviteOnly ) {
			s = s + ChatColor.GRAY + ", " + ChatColor.RED + close;
		}
		s = s.replaceFirst(", ", "");

		sender.sendMessage(plugin.getPrefix() + plugin.getMsg().getMessage("cmdList") + " " + s);

		return false;
	}

}
