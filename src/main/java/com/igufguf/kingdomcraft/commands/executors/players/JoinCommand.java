package com.igufguf.kingdomcraft.commands.executors.players;

import com.igufguf.kingdomcraft.commands.CommandBase;
import com.igufguf.kingdomcraft.KingdomCraft;
import com.igufguf.kingdomcraft.commands.CommandHandler;
import com.igufguf.kingdomcraft.managers.FlagManager;
import com.igufguf.kingdomcraft.objects.KingdomObject;
import com.igufguf.kingdomcraft.objects.KingdomUser;
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
public class JoinCommand extends CommandBase {

	private final KingdomCraft plugin;

	public JoinCommand(KingdomCraft plugin) {
		super("join", "kingdom.join", true);

		this.plugin = plugin;

		plugin.getCmdHandler().register(this);
	}
	
	@Override
	public List<String> tabcomplete(CommandSender sender, String[] args) {
		if ( args.length == 2 ) {
			KingdomUser user = plugin.getApi().getUserManager().getUser((Player) sender);

			List<String> kingdoms = new ArrayList<>();
			for ( KingdomObject kd : plugin.getApi().getKingdomManager().getKingdoms() ) {
				if ( kd.getName().toLowerCase().startsWith(args[1].toLowerCase()) ) {
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
		Player p = (Player) sender;
		
		if ( args.length != 1 ) {
			plugin.getMsg().send(sender, "cmdDefaultUsage");
			return false;
		}
		if ( plugin.getApi().getKingdomManager().getKingdom(args[0]) == null ) {
			plugin.getMsg().send(sender, "cmdDefaultKingdomNotExist", args[0]);
			return false;
		}
		if ( plugin.getApi().getUserManager().getUser(p).getKingdom() != null ) {
			plugin.getMsg().send(sender, "cmdJoinAlready");
			return false;
		}
		
		KingdomObject kingdom = plugin.getApi().getKingdomManager().getKingdom(args[0]);
		KingdomUser user = plugin.getApi().getUserManager().getUser(p);
		
		if ( plugin.getApi().getFlagManager().getFlagBoolean(kingdom, FlagManager.INVITE_ONLY) &&
				!user.hasInList("invites", kingdom.getName()) && !p.hasPermission("kingdom.join." + kingdom.getName()) ) {

			plugin.getMsg().send(sender, "cmdJoinInviteOnly", kingdom.getName());

			for ( Player member : plugin.getApi().getKingdomManager().getOnlineMembers(kingdom) ) {
				plugin.getMsg().send(member, "cmdJoinInviteOnlyMembers", p.getName());
			}

			return false;
		}

		if ( kingdom.hasData("max-members") && plugin.getApi().getKingdomManager().getMembers(kingdom).size() >= (int) kingdom.getData("max-members") ) {
			plugin.getMsg().send(sender, "cmdJoinFull", kingdom.getName());
			return false;
		}

		for ( Player member : plugin.getApi().getKingdomManager().getOnlineMembers(kingdom) ) {
			plugin.getMsg().send(member, "cmdJoinSuccessMembers", p.getName());
		}

		plugin.getApi().getUserManager().setKingdom(user, kingdom);

		plugin.getMsg().send(sender, "cmdJoinSuccess", kingdom.getName());

		if ( kingdom.getSpawn() != null && plugin.getCfg().getBoolean("spawn-on-kingdom-join") ) {
			p.teleport(kingdom.getSpawn());
		}

		return false;
	}

}
