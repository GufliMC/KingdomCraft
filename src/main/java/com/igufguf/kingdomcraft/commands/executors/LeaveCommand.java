package com.igufguf.kingdomcraft.commands.executors;

import com.igufguf.kingdomcraft.commands.CommandBase;
import com.igufguf.kingdomcraft.objects.KingdomObject;
import com.igufguf.kingdomcraft.KingdomCraft;
import com.igufguf.kingdomcraft.commands.CommandHandler;
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
public class LeaveCommand extends CommandBase {

	public LeaveCommand() {
		super("leave", "kingdom.leave", true);
		
		CommandHandler.register(this);
	}
	
	@Override
	public ArrayList<String> tabcomplete(String[] args) {
		return null;
	}
	
	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Player p = (Player) sender;
		
		if ( args.length != 0 ) {
			KingdomCraft.getMsg().send(sender, "cmdDefaultUsage");
			return false;
		}

		KingdomUser user = KingdomCraft.getApi().getUser(p);
		KingdomObject kingdom = user.getKingdom();

		if ( kingdom == null ) {
			KingdomCraft.getMsg().send(sender, "cmdDefaultSenderNoKingdom");
			return false;
		}

		KingdomCraft.getApi().setKingdom(user, null);

		KingdomCraft.getMsg().send(sender, "cmdLeaveSuccess", kingdom.getName());

		for ( Player member : kingdom.getOnlineMembers() ) {
			KingdomCraft.getMsg().send(member, "cmdLeaveSuccessMembers", p.getName());
		}

		if ( KingdomCraft.getConfg().has("spawn-on-kingdom-leave") && KingdomCraft.getConfg().getBoolean("spawn-on-kingdom-leave") ) {
			Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "spawn " + p.getName());
		}
		return false;
	}
	
}
