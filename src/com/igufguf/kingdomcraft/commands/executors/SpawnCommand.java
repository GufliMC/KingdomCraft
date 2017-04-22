package com.igufguf.kingdomcraft.commands.executors;

import com.igufguf.kingdomcraft.commands.CommandBase;
import com.igufguf.kingdomcraft.KingdomCraft;
import com.igufguf.kingdomcraft.commands.CommandHandler;
import com.igufguf.kingdomcraft.objects.KingdomObject;
import com.igufguf.kingdomcraft.KingdomCraftMessages;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

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
public class SpawnCommand extends CommandBase {

	public static ArrayList<String> teleporting = new ArrayList<String>();
	
	public SpawnCommand() {
		super("spawn", "kingdom.spawn", true);
		addAliasses("s");
		
		CommandHandler.register(this);
	}
	
	@Override
	public ArrayList<String> tabcomplete(String[] args) {
		if ( args.length == 2 ) {
			ArrayList<String> kingdoms = new ArrayList<>();
			for ( KingdomObject kd : KingdomCraft.getApi().getKingdoms() ) {
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
			sender.sendMessage(KingdomCraft.prefix + KingdomCraft.getMsg().getMessage("cmdDefaultUsage"));
			return false;
		}
		if (KingdomCraft.getApi().getUser(p).getKingdom() == null ) {
			sender.sendMessage(KingdomCraft.prefix + KingdomCraft.getMsg().getMessage("cmdDefaultSenderNoKingdom"));
			return false;
		}
		
		if ( args.length == 1 && p.hasPermission(this.permission + ".other")) {
			if ( KingdomCraft.getApi().getKingdom(args[0]) == null ) {
				sender.sendMessage(KingdomCraft.prefix + KingdomCraft.getMsg().getMessage("cmdDefaultKingdomNotExist", args[0]));
				return false;
			}
			KingdomObject kingdom = KingdomCraft.getApi().getKingdom(args[0]);
			p.teleport(kingdom.getSpawn());
			sender.sendMessage(KingdomCraft.prefix + KingdomCraft.getMsg().getMessage("cmdSpawnTeleported",
					((int) kingdom.getSpawn().getX()) + ", " + ((int) kingdom.getSpawn().getY()) + ", "
							+ ((int) kingdom.getSpawn().getZ())));
		} else {
			final KingdomObject kingdom = KingdomCraft.getApi().getUser(p).getKingdom();
			
			if ( kingdom.getSpawn() != null ) {
				int tpdelay = KingdomCraft.getConfg().has("tp-delay") ? KingdomCraft.getConfg().getInt("tp-delay") : 0;
				
				if ( tpdelay > 0 ) {
					sender.sendMessage(KingdomCraft.prefix + KingdomCraft.getMsg().getMessage("cmdSpawnTeleportStarting", tpdelay + ""));
				}
				
				teleporting.add(p.getName());
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(KingdomCraft.getPlugin(KingdomCraft.class), new Runnable() {
		            @Override
		            public void run() {
		            	if ( p.isOnline() && teleporting.contains(p.getName())) {
		            		p.teleport(kingdom.getSpawn());
							p.sendMessage(KingdomCraft.prefix + KingdomCraft.getMsg().getMessage("cmdSpawnTeleported",
									((int) kingdom.getSpawn().getX()) + ", " + ((int) kingdom.getSpawn().getY()) + ", "
											+ ((int) kingdom.getSpawn().getZ())));
			            	teleporting.remove(p.getName());
		            	}
		            }
		        }, tpdelay * 20);
			} else {
				sender.sendMessage(KingdomCraft.prefix + KingdomCraft.getMsg().getMessage("cmdSpawnNotExists", kingdom.getName()));
			}
		}
		
		return false;
	}
	
}
