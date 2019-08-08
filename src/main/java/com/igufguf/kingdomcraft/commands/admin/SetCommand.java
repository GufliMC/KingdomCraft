package com.igufguf.kingdomcraft.commands.admin;

import com.igufguf.kingdomcraft.api.models.commands.CommandBase;
import com.igufguf.kingdomcraft.api.models.kingdom.Kingdom;
import com.igufguf.kingdomcraft.api.models.kingdom.KingdomUser;
import com.igufguf.kingdomcraft.KingdomCraft;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
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
public class SetCommand extends CommandBase {

	private final KingdomCraft plugin;

	public SetCommand(KingdomCraft plugin) {
		super("set", "kingdom.set", false, "<player> <kingdom>");

		this.plugin = plugin;
	}

	@Override
	public List<String> tabcomplete(CommandSender sender, String[] args) {
		if ( args.length == 1 ) {
			return Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName)
					.filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase())).collect(Collectors.toList());
		}

		if ( args.length == 2 ) {
			KingdomUser user = plugin.getApi().getUserHandler().getUser(Bukkit.getPlayerExact(args[0]));

			List<String> kingdoms = new ArrayList<>();
			for ( Kingdom kd : plugin.getApi().getKingdomHandler().getKingdoms() ) {
				if ( kd.getName().toLowerCase().startsWith(args[1].toLowerCase()) ) {
					if ( user != null && user.getKingdom() != null && kd.getName().equals(user.getKingdom()) ) continue;
					kingdoms.add(kd.getName());
				}
			}
			return kingdoms;
		}
		return null;
	}
	
	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if ( args.length != 2 ) {
			return false;
		}
		if ( plugin.getApi().getKingdomHandler().getKingdom(args[1]) == null ) {
			plugin.getMsg().send(sender, "cmdDefaultKingdomNotExist", args[1]);
			return true;
		}
		String username = args[0];
		KingdomUser user = plugin.getApi().getUserHandler().getUser(username);
		
		if ( user == null ) {
			plugin.getMsg().send(sender, "cmdDefaultNoPlayer", username);
			return true;
		}
		
		Kingdom kingdom = plugin.getApi().getKingdomHandler().getKingdom(args[1]);

		plugin.getApi().getUserHandler().setKingdom(user, kingdom);
		
		if ( user.getPlayer() != null ) {
			plugin.getMsg().send(user.getPlayer(), "cmdSetTarget", kingdom.getName());
		}
		plugin.getMsg().send(sender, "cmdSetSender", user.getName(), kingdom.getName());

		if ( user.getPlayer() != null && kingdom.getSpawn() != null && plugin.getCfg().has("spawn-on-kingdom-join")
				&& plugin.getCfg().getBoolean("spawn-on-kingdom-join") ) {
			user.getPlayer().teleport(kingdom.getSpawn());
		}

		return true;
	}

}
