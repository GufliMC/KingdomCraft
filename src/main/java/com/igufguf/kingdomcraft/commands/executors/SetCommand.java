package com.igufguf.kingdomcraft.commands.executors;

import com.igufguf.kingdomcraft.commands.CommandBase;
import com.igufguf.kingdomcraft.objects.KingdomObject;
import com.igufguf.kingdomcraft.objects.KingdomUser;
import com.igufguf.kingdomcraft.KingdomCraft;
import com.igufguf.kingdomcraft.commands.CommandHandler;
import com.igufguf.kingdomcraft.KingdomCraftMessages;
import org.bukkit.command.CommandSender;

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
public class SetCommand extends CommandBase {

	public SetCommand() {
		super("set", "kingdom.set", false);
		
		CommandHandler.register(this);
	}
	
	@Override
	public ArrayList<String> tabcomplete(String[] args) {
		if ( args.length == 3 ) {
			ArrayList<String> kingdoms = new ArrayList<>();
			for ( KingdomObject kd : KingdomCraft.getApi().getKingdoms() ) {
				if ( kd.getName().toLowerCase().startsWith(args[2].toLowerCase()) ) kingdoms.add(kd.getName());
			}
			return kingdoms;
		}
		return null;
	}
	
	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if ( args.length != 2 ) {
			KingdomCraft.getMsg().send(sender, "cmdDefaultUsage");
			return false;
		}
		if ( KingdomCraft.getApi().getKingdom(args[1]) == null ) {
			KingdomCraft.getMsg().send(sender, "cmdDefaultKingdomNotExist", args[1]);
			return false;
		}
		String username = args[0];
		KingdomUser user = KingdomCraft.getApi().getUser(username);
		
		if ( user == null ) {
			KingdomCraft.getMsg().send(sender, "cmdDefaultNoPlayer", username);
			return false;
		}
		
		KingdomObject kingdom = KingdomCraft.getApi().getKingdom(args[1]);

		KingdomCraft.getApi().setKingdom(user, kingdom);
		
		if ( user.getPlayer() != null ) {
			KingdomCraft.getMsg().send(user.getPlayer(), "cmdSetTarget", kingdom.getName());
		}
		KingdomCraft.getMsg().send(sender, "cmdSetSender", user.getName(), kingdom.getName());

		if ( user.getPlayer() != null && kingdom.getSpawn() != null && KingdomCraft.getConfg().has("spawn-on-kingdom-join")
				&& KingdomCraft.getConfg().getBoolean("spawn-on-kingdom-join") ) {
			user.getPlayer().teleport(kingdom.getSpawn());
		}

		return false;
	}

}
