package com.igufguf.kingdomcraft.commands.executors;

import com.igufguf.kingdomcraft.commands.CommandBase;
import com.igufguf.kingdomcraft.objects.KingdomObject;
import com.igufguf.kingdomcraft.objects.KingdomUser;
import com.igufguf.kingdomcraft.KingdomCraft;
import com.igufguf.kingdomcraft.commands.CommandHandler;
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
public class SetCommand extends CommandBase {

	private final KingdomCraft plugin;

	public SetCommand(KingdomCraft plugin) {
		super("set", "kingdom.set", false);

		this.plugin = plugin;

		plugin.getCmdHandler().register(this);
	}
	
	@Override
	public List<String> tabcomplete(CommandSender sender, String[] args) {
		if ( args.length == 3 ) {
			KingdomUser user = plugin.getApi().getUserManager().getUser((Player) sender);

			List<String> kingdoms = new ArrayList<>();
			for ( KingdomObject kd : plugin.getApi().getKingdomManager().getKingdoms() ) {
				if ( kd.getName().toLowerCase().startsWith(args[2].toLowerCase()) ) {
					if ( user.getKingdom() != null && kd.getName().equals(user.getKingdom()) ) continue;
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
			plugin.getMsg().send(sender, "cmdDefaultUsage");
			return false;
		}
		if ( plugin.getApi().getKingdomManager().getKingdom(args[1]) == null ) {
			plugin.getMsg().send(sender, "cmdDefaultKingdomNotExist", args[1]);
			return false;
		}
		String username = args[0];
		KingdomUser user = plugin.getApi().getUserManager().getUser(username);
		
		if ( user == null ) {
			plugin.getMsg().send(sender, "cmdDefaultNoPlayer", username);
			return false;
		}
		
		KingdomObject kingdom = plugin.getApi().getKingdomManager().getKingdom(args[1]);

		plugin.getApi().getUserManager().setKingdom(user, kingdom);
		
		if ( user.getPlayer() != null ) {
			plugin.getMsg().send(user.getPlayer(), "cmdSetTarget", kingdom.getName());
		}
		plugin.getMsg().send(sender, "cmdSetSender", user.getName(), kingdom.getName());

		if ( user.getPlayer() != null && kingdom.getSpawn() != null && plugin.getCfg().has("spawn-on-kingdom-join")
				&& plugin.getCfg().getBoolean("spawn-on-kingdom-join") ) {
			user.getPlayer().teleport(kingdom.getSpawn());
		}

		return false;
	}

}
