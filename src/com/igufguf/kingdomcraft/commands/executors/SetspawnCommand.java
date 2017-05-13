package com.igufguf.kingdomcraft.commands.executors;

import com.igufguf.kingdomcraft.commands.CommandBase;
import com.igufguf.kingdomcraft.objects.KingdomObject;
import com.igufguf.kingdomcraft.KingdomCraft;
import com.igufguf.kingdomcraft.commands.CommandHandler;
import com.igufguf.kingdomcraft.KingdomCraftMessages;
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
public class SetspawnCommand extends CommandBase {

	public SetspawnCommand() {
		super("setspawn", "kingdom.setspawn", true);
		
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
		Player p = (Player) sender;
		
		if ( args.length == 0 ) {
			if ( KingdomCraft.getApi().getUser(p).getKingdom() == null ) {
				sender.sendMessage(KingdomCraft.prefix + KingdomCraft.getMsg().getMessage("cmdDefaultSenderNoKingdom"));
				return false;
			}
			
			KingdomObject kingdom = KingdomCraft.getApi().getUser(p).getKingdom();
			kingdom.setSpawn(p.getLocation());
			sender.sendMessage(KingdomCraft.prefix + KingdomCraft.getMsg().getMessage("cmdSetspawnSuccess",
					((int) p.getLocation().getX()) + ", " + ((int) p.getLocation().getY())
							+ ", " + ((int) p.getLocation().getZ())));
			
		} else if ( args.length == 1 ) {
			if ( !p.hasPermission(this.permission + ".other") ) {
				sender.sendMessage(KingdomCraft.prefix + KingdomCraft.getMsg().getMessage("noPermissionCmd"));
				return false;
			}
			if ( KingdomCraft.getApi().getKingdom(args[0]) == null ) {
				sender.sendMessage(KingdomCraft.prefix + KingdomCraft.getMsg().getMessage("cmdDefaultKingdomNotExist", args[0]));
				return false;
			}

			KingdomObject kingdom = KingdomCraft.getApi().getKingdom(args[0]);
			kingdom.setSpawn(p.getLocation());
			sender.sendMessage(KingdomCraft.prefix + KingdomCraft.getMsg().getMessage("cmdSetspawnSuccess", kingdom.getName(),
					((int) p.getLocation().getX()) + ", " + ((int) p.getLocation().getY()) + ", "
							+ ((int) p.getLocation().getZ())));
			
		} else {
			sender.sendMessage(KingdomCraft.prefix + KingdomCraft.getMsg().getMessage("cmdDefaultUsage"));
		}
		return false;
	}
}
