package com.igufguf.kingdomcraft.commands.executors;

import com.igufguf.kingdomcraft.commands.CommandBase;
import com.igufguf.kingdomcraft.KingdomCraft;
import com.igufguf.kingdomcraft.commands.CommandHandler;
import com.igufguf.kingdomcraft.objects.KingdomObject;
import com.igufguf.kingdomcraft.objects.KingdomUser;
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
public class JoinCommand extends CommandBase {

	public JoinCommand() {
		super("join", "kingdom.join", true);
		
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
		
		if ( args.length != 1 ) {
			sender.sendMessage(KingdomCraft.prefix + KingdomCraft.getMsg().getMessage("cmdDefaultUsage"));
			return false;
		}
		if ( KingdomCraft.getApi().getKingdom(args[0]) == null ) {
			sender.sendMessage(KingdomCraft.prefix + KingdomCraft.getMsg().getMessage("cmdDefaultKingdomNotExist", args[0]));
			return false;
		}
		if ( KingdomCraft.getApi().getUser(p).getKingdom() != null ) {
			sender.sendMessage(KingdomCraft.prefix + KingdomCraft.getMsg().getMessage("cmdJoinAlready"));
			return false;
		}
		
		KingdomObject kingdom = KingdomCraft.getApi().getKingdom(args[0]);
		KingdomUser user = KingdomCraft.getApi().getUser(p);
		
		if ( kingdom.hasInList("flags", "closed") ) {
			if ( !user.hasInList("invites", kingdom.getName()) && !p.hasPermission("kingdom.join." + kingdom.getName()) ) {
				sender.sendMessage(KingdomCraft.prefix + KingdomCraft.getMsg().getMessage("cmdJoinClosed", kingdom.getName()));
				for ( Player member : kingdom.getOnlineMembers() ) {
					member.sendMessage(KingdomCraft.prefix + KingdomCraft.getMsg().getMessage("cmdJoinClosedMembers", p.getName()));
				}
				return false;
			}
		}

		if ( kingdom.hasData("max-members") && kingdom.getMembers().size() >= (int) kingdom.getData("max-members") ) {
			sender.sendMessage(KingdomCraft.prefix + KingdomCraft.getMsg().getMessage("cmdJoinFull", kingdom.getName()));
			return false;
		}

		for ( Player member : kingdom.getOnlineMembers() ) {
			member.sendMessage(KingdomCraft.prefix + KingdomCraft.getMsg().getMessage("cmdJoinSuccessMembers", p.getName()));
		}

		KingdomCraft.getApi().setKingdom(user, kingdom);

		sender.sendMessage(KingdomCraft.prefix + KingdomCraft.getMsg().getMessage("cmdJoinSuccess", kingdom.getName()));

		if ( kingdom.getSpawn() != null && KingdomCraft.getConfg().has("spawn-on-kingdom-join")
				&& KingdomCraft.getConfg().getBoolean("spawn-on-kingdom-join") ) {
			p.teleport(kingdom.getSpawn());
		}

		return false;
	}

}
