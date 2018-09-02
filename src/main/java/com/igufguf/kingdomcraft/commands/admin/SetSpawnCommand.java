package com.igufguf.kingdomcraft.commands.admin;

import com.igufguf.kingdomcraft.api.models.commands.CommandBase;
import com.igufguf.kingdomcraft.api.models.kingdom.Kingdom;
import com.igufguf.kingdomcraft.KingdomCraft;
import com.igufguf.kingdomcraft.api.models.kingdom.KingdomUser;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
public class SetSpawnCommand extends CommandBase {

	private final KingdomCraft plugin;

	public SetSpawnCommand(KingdomCraft plugin) {
		super("setspawn", "kingdom.setspawn", true);

		this.plugin = plugin;
	}
	
	@Override
	public List<String> tabcomplete(CommandSender sender, String[] args) {
		if ( !sender.hasPermission("kingdom.setspawn.other") ) return null;
		if ( args.length == 2 ) {
			List<String> kingdoms = new ArrayList<>();
			for ( Kingdom kd : plugin.getApi().getKingdomHandler().getKingdoms() ) {
				if ( kd.getName().toLowerCase().startsWith(args[1].toLowerCase()) ) kingdoms.add(kd.getName());
			}
			return kingdoms;
		}
		return null;
	}
	
	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Player p = (Player) sender;
		
		if ( args.length == 0 ) {
			if ( plugin.getApi().getUserHandler().getUser(p).getKingdom() == null ) {
				plugin.getMsg().send(sender, "cmdDefaultSenderNoKingdom");
				return false;
			}

			KingdomUser user = plugin.getApi().getUserHandler().getUser(p);
			Kingdom kingdom = plugin.getApi().getUserHandler().getKingdom(user);

			kingdom.setSpawn(p.getLocation());
			plugin.getMsg().send(sender, "cmdSetSpawnSuccess", kingdom.getName(),
					((int) p.getLocation().getX()) + ", " + ((int) p.getLocation().getY())
							+ ", " + ((int) p.getLocation().getZ()));
			
		} else if ( args.length == 1 ) {
			if ( !p.hasPermission(this.permission + ".other") ) {
				plugin.getMsg().send(sender, "noPermissionCmd");
				return false;
			}

			if ( plugin.getApi().getKingdomHandler().getKingdom(args[0]) == null ) {
				plugin.getMsg().send(sender, "cmdDefaultKingdomNotExist", args[0]);
				return false;
			}

			Kingdom kingdom = plugin.getApi().getKingdomHandler().getKingdom(args[0]);
			kingdom.setSpawn(p.getLocation());
			plugin.getMsg().send(sender, "cmdSetSpawnSuccess", kingdom.getName(),
					((int) p.getLocation().getX()) + ", " + ((int) p.getLocation().getY()) + ", "
							+ ((int) p.getLocation().getZ()));
			
		} else {
			plugin.getMsg().send(sender, "cmdDefaultUsage");
		}
		return false;
	}
}
