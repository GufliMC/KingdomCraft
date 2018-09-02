package com.igufguf.kingdomcraft.commands.members;

import com.igufguf.kingdomcraft.api.models.commands.CommandBase;
import com.igufguf.kingdomcraft.KingdomCraft;
import com.igufguf.kingdomcraft.managers.TeleportManager;
import com.igufguf.kingdomcraft.api.models.kingdom.Kingdom;
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
public class SpawnCommand extends CommandBase {

	private final KingdomCraft plugin;

	public SpawnCommand(KingdomCraft plugin) {
		super("spawn", "kingdom.spawn", true);
		addAliasses("s");

		this.plugin = plugin;
	}
	
	@Override
	public List<String> tabcomplete(CommandSender sender, String[] args) {
		if ( !sender.hasPermission("kingdom.spawn.other") ) return null;
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
		final Player p = (Player) sender;
		
		if ( args.length > 1 ) {
			plugin.getMsg().send(sender, "cmdDefaultUsage");
			return false;
		}
		if ( plugin.getApi().getUserHandler().getUser(p).getKingdom() == null ) {
			plugin.getMsg().send(sender, "cmdDefaultSenderNoKingdom");
			return false;
		}
		
		if ( args.length == 1 && p.hasPermission(this.permission + ".other") ) {
			Kingdom kingdom = plugin.getApi().getKingdomHandler().getKingdom(args[0]);

			if ( kingdom == null ) {
				plugin.getMsg().send(sender, "cmdDefaultKingdomNotExist", args[0]);
				return false;
			}

			if ( kingdom.getSpawn() == null ) {
				plugin.getMsg().send(sender, "cmdSpawnNotExists", kingdom.getName());
				return false;
			}

			p.teleport(kingdom.getSpawn());
			plugin.getMsg().send(sender, "cmdSpawnTeleported",
					((int) kingdom.getSpawn().getX()) + ", " +
					((int) kingdom.getSpawn().getY()) + ", " +
					((int) kingdom.getSpawn().getZ()));

			return false;
		}

		KingdomUser user = plugin.getApi().getUserHandler().getUser(p);
		final Kingdom kingdom = plugin.getApi().getUserHandler().getKingdom(user);

		if ( kingdom.getSpawn() == null ) {
			plugin.getMsg().send(sender, "cmdSpawnNotExists", kingdom.getName());
			return false;
		}

		TeleportManager tm = plugin.getTeleportManager();
		if ( tm.isTeleporting(user) ) {
			return false;
		}

		int tpdelay = plugin.getCfg().has("tp-delay") ? plugin.getCfg().getInt("tp-delay") : 0;

		if ( tpdelay > 0 ) {
			plugin.getMsg().send(sender, "cmdSpawnTeleportStarting", tpdelay + "");
		}

		String msg = plugin.getMsg().getMessage("cmdSpawnTeleported",
				((int) kingdom.getSpawn().getX()) + ", " +
				((int) kingdom.getSpawn().getY()) + ", " +
				((int) kingdom.getSpawn().getZ()));

		tm.startTeleporter(user, kingdom.getSpawn(), msg, tpdelay);

		return false;
	}
	
}
