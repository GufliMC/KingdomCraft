package com.igufguf.kingdomcraft.commands.executors;

import com.igufguf.kingdomcraft.KingdomCraft;
import com.igufguf.kingdomcraft.commands.CommandBase;
import com.igufguf.kingdomcraft.commands.CommandHandler;
import com.igufguf.kingdomcraft.objects.KingdomObject;
import com.igufguf.kingdomcraft.objects.KingdomRank;
import com.igufguf.kingdomcraft.objects.KingdomUser;
import com.igufguf.kingdomcraft.KingdomCraftMessages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

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
public class SetrankCommand extends CommandBase {

	private KingdomCraft plugin;

	public SetrankCommand(KingdomCraft plugin) {
		super("setrank", "kingdom.setrank", false);

		this.plugin = plugin;

		plugin.getCmdHandler().register(this);
	}

	@Override
	public ArrayList<String> tabcomplete(String[] args) {
		if ( args.length == 3 ) {
			String username = args[1];
			KingdomUser user = plugin.getApi().getUserManager().getUser(username);
			KingdomObject kingdom = plugin.getApi().getUserManager().getKingdom(user);

			if ( user != null && user.getKingdom() != null ) {
				ArrayList<String> ranks = new ArrayList<>();
				for ( KingdomRank rank : kingdom.getRanks() ) {
                    if ( rank.getName().toLowerCase().startsWith(args[3].toLowerCase()) ) ranks.add(rank.getName());
                }
				return ranks;
			}
		}
		return null;
	}
	
	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if ( args.length != 2 ) {
			plugin.getMsg().send(sender, "cmdDefaultUsage");
			return false;
		}
		String username = args[0];
		KingdomUser user = plugin.getApi().getUserManager().getUser(username);
		
		if ( user == null ) {
			plugin.getMsg().send(sender, "cmdDefaultNoPlayer");
			return false;
		}
		if ( user.getKingdom() == null ) {
			plugin.getMsg().send(sender, "cmdDefaultTargetNoKingdom");
			return false;
		}

		KingdomObject kingdom = plugin.getApi().getUserManager().getKingdom(user);

		KingdomRank rank = null;
		for ( KingdomRank r : kingdom.getRanks() ) {
			if ( r.getName().equalsIgnoreCase(args[1]) ) rank = r;
		}

		if ( rank == null ) {
			plugin.getMsg().send(sender, "cmdRankNotExist");
			return false;
		}

		if ( (sender instanceof Player) && !user.getKingdom().equals(plugin.getApi().getUserManager().getUser((Player) sender).getKingdom())
				&& !sender.hasPermission(this.permission + ".other") ) {
			plugin.getMsg().send(sender, "cmdNoPermissionCmd");
			return false;
		}

		user.setData("rank", rank.getName());
		plugin.getMsg().send(sender, "cmdRankSenderChange", user.getName(), rank.getName());

		if ( user.getPlayer() != null ) {
			plugin.getMsg().send(sender, "cmdRankTargetChange", rank.getName());
		}
		
		//save new rank because user is offline
		if ( plugin.getApi().getUserManager().getUser(username) == null ) {
			plugin.getApi().getUserManager().save(user);
		}
		
		return false;
	}

}
