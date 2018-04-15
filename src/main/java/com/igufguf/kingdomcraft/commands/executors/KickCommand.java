package com.igufguf.kingdomcraft.commands.executors;

import com.igufguf.kingdomcraft.KingdomCraft;
import com.igufguf.kingdomcraft.commands.CommandBase;
import com.igufguf.kingdomcraft.commands.CommandHandler;
import com.igufguf.kingdomcraft.objects.KingdomObject;
import com.igufguf.kingdomcraft.objects.KingdomUser;
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
public class KickCommand extends CommandBase {

	public KickCommand() {
		super("kick", "kingdom.kick", true);
		
		CommandHandler.register(this);
	}
	
	@Override
	public ArrayList<String> tabcomplete(String[] args) {
		return null;
	}
	
	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Player p = (Player) sender;
		
		if ( args.length != 1 ) {
			KingdomCraft.getMsg().send(sender, "cmdDefaultUsage");
			return false;
		}
		String username = args[0];
		KingdomUser user = KingdomCraft.getApi().getUser(username);
		
		if ( user == null ) {
			KingdomCraft.getMsg().send(sender, "cmdDefaultNoPlayer", username);
			return false;
		}
		if ( user.getKingdom() == null ) {
			KingdomCraft.getMsg().send(sender, "cmdDefaultTargetNoKingdom", user.getName());
			return false;
		}
		
		if ( p.hasPermission(this.permission + ".other") || (KingdomCraft.getApi().getUser(p) != null && KingdomCraft.getApi().getUser(p).getKingdom() == user.getKingdom()) ) {
			KingdomObject kingdom = user.getKingdom();

			KingdomCraft.getApi().setKingdom(user, null);

			for ( Player member : kingdom.getOnlineMembers() ) {
				KingdomCraft.getMsg().send(member, "cmdLeaveSuccessMembers", user.getName());
			}

			KingdomCraft.getMsg().send(sender, "cmdKickSender", user.getName(), kingdom.getName());

			if ( user.getPlayer() != null ) {
				KingdomCraft.getMsg().send(user.getPlayer(), "cmdKickTarget", kingdom.getName());
			}
			
			if ( KingdomCraft.getConfg().getBoolean("spawn-on-kingdom-leave") && user.getPlayer() != null ) {
				Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "spawn " + user.getName());
			}
		} else {
			KingdomCraft.getMsg().send(sender, "noPermissionCmd");
		}
		
		//save user when player is not online
		if ( user.getPlayer() == null ) {
			KingdomCraft.getPlugin().save(user);
		}
		
		return true;
	}
}
