package com.igufguf.kingdomcraft.commands.admin;

import com.igufguf.kingdomcraft.KingdomCraft;
import com.igufguf.kingdomcraft.api.models.commands.CommandBase;
import com.igufguf.kingdomcraft.api.models.kingdom.Kingdom;
import com.igufguf.kingdomcraft.api.models.kingdom.KingdomRank;
import com.igufguf.kingdomcraft.api.models.kingdom.KingdomUser;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
public class SetRankCommand extends CommandBase {

	private KingdomCraft plugin;

	public SetRankCommand(KingdomCraft plugin) {
		super("setrank", "kingdom.setrank", false, "<player> <rank>");

		this.plugin = plugin;
	}

	@Override
	public List<String> tabcomplete(CommandSender sender, String[] args) {
		if ( args.length == 1 ) {
			if ( sender.hasPermission(this.getPermission() + ".other") ) {
				return Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName)
						.filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase())).collect(Collectors.toList());
			} else if ( sender instanceof Player ) {
				KingdomUser user = plugin.getApi().getUserHandler().getUser((Player) sender);
				Kingdom kingdom = plugin.getApi().getUserHandler().getKingdom(user);
				if ( user.getKingdom() == null ) return null;

				return plugin.getApi().getKingdomHandler().getOnlineMembers(kingdom).stream().filter(p -> p != sender).map(HumanEntity::getName)
						.filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase())).collect(Collectors.toList());
			}
		}

		if ( args.length == 2 ) {
			String username = args[0];
			KingdomUser user = plugin.getApi().getUserHandler().getUser(username);
			if ( user == null ) return null;

			Kingdom kingdom = plugin.getApi().getUserHandler().getKingdom(user);

			if ( user.getKingdom() == null ) return null;

			List<String> ranks = new ArrayList<>();
			for ( KingdomRank rank : kingdom.getRanks() ) {
				if ( !rank.getName().toLowerCase().startsWith(args[1].toLowerCase()) ) continue;
				if ( user.getRank() != null && user.getRank().equals(rank.getName()) ) continue;

				ranks.add(rank.getName());
			}
			return ranks;
		}
		return null;
	}
	
	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if ( args.length != 2 ) {
			return false;
		}

		String username = args[0];
		KingdomUser user = plugin.getApi().getUserHandler().getUser(username);
		
		if ( user == null ) {
			plugin.getMsg().send(sender, "cmdDefaultNoPlayer");
			return true;
		}
		if ( user.getKingdom() == null ) {
			plugin.getMsg().send(sender, "cmdDefaultTargetNoKingdom");
			return true;
		}

		Kingdom kingdom = plugin.getApi().getUserHandler().getKingdom(user);

		KingdomRank rank = null;
		for ( KingdomRank r : kingdom.getRanks() ) {
			if ( r.getName().equalsIgnoreCase(args[1]) ) rank = r;
		}

		if ( rank == null ) {
			plugin.getMsg().send(sender, "cmdRankNotExist");
			return true;
		}

		if ( (sender instanceof Player) && !user.getKingdom().equals(plugin.getApi().getUserHandler().getUser((Player) sender).getKingdom())
				&& !sender.hasPermission(this.getPermission() + ".other") ) {
			plugin.getMsg().send(sender, "cmdNoPermissionCmd");
			return true;
		}

		plugin.getApi().getUserHandler().setRank(user, rank);
		plugin.getMsg().send(sender, "cmdRankSenderChange", user.getName(), rank.getName());

		if ( user.getPlayer() != null ) {
			plugin.getMsg().send(user.getPlayer(), "cmdRankTargetChange", rank.getName());
		}
		
		//save new rank because user is offline
		if ( plugin.getApi().getUserHandler().getUser(username) == null ) {
			plugin.getApi().getUserHandler().save(user);
		}
		
		return true;
	}

}
