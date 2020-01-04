package com.igufguf.kingdomcraft.commands.players;

import com.igufguf.kingdomcraft.api.models.commands.CommandBase;
import com.igufguf.kingdomcraft.KingdomCraft;
import com.igufguf.kingdomcraft.api.models.kingdom.Kingdom;
import com.igufguf.kingdomcraft.api.models.kingdom.KingdomRelation;
import com.igufguf.kingdomcraft.api.models.kingdom.KingdomUser;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
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
public class InfoCommand extends CommandBase {

	private final KingdomCraft plugin;

	public InfoCommand(KingdomCraft plugin) {
		super("info", null, false, "[<player>|<kingdom>]");
		addAliasses("i");

		this.plugin = plugin;
	}
	
	@Override
	public List<String> tabcomplete(CommandSender sender, String[] args) {
		if ( args.length == 1 ) {
			List<String> kingdoms = new ArrayList<>();
			for ( Kingdom kd : plugin.getApi().getKingdomHandler().getKingdoms() ) {
				if ( kd.getName().toLowerCase().startsWith(args[0].toLowerCase()) ) kingdoms.add(kd.getName());
			}
			for ( Player p : Bukkit.getOnlinePlayers() ) {
				if ( p.getName().toLowerCase().startsWith(args[0].toLowerCase()) ) kingdoms.add(p.getName());
			}
			return kingdoms;
		}
		return null;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean execute(CommandSender sender, String[] args) {
		
		if ( args.length > 1 || (args.length == 0 && !(sender instanceof Player))) {
			return false;
		}
		
		if ( args.length == 1 && plugin.getApi().getKingdomHandler().getKingdom(args[0]) == null ) {
			if ( Bukkit.getPlayerExact(args[0]) != null ) {
				Player target = Bukkit.getPlayer(args[0]);
				
				showPlayerInfo(sender, target);
				return true;
			}
			plugin.getMsg().send(sender, "cmdDefaultKingdomNotExist", args[0]);
			return true;
		}
		Kingdom kingdom;
		
		if ( args.length == 1 ) {
			kingdom = plugin.getApi().getKingdomHandler().getKingdom(args[0]);
		} else {
			KingdomUser user = plugin.getApi().getUserHandler().getUser((Player) sender);
			kingdom = plugin.getApi().getUserHandler().getKingdom(user);
		}
		
		if ( kingdom == null ) {
			plugin.getMsg().send(sender, "cmdDefaultTargetNoKingdom");
			return true;
		}
		
		showKingdomInfo(sender, kingdom);
		
		return true;
	}
	
	protected void showKingdomInfo(CommandSender sender, Kingdom kingdom) {
		plugin.getMsg().send(sender, "cmdInfoKingdom", kingdom.getName());
		
		sender.sendMessage(ChatColor.GRAY + "Display: " + kingdom.getDisplay());

		// sort allys/enemys
		StringBuilder friendlysb = new StringBuilder();
		StringBuilder enemysb = new StringBuilder();

		HashMap<Kingdom, KingdomRelation> relations = plugin.getApi().getRelationHandler().getRelations(kingdom);
		for ( Kingdom ko : relations.keySet() ) {
			if ( relations.get(ko) == KingdomRelation.FRIENDLY ) {
			    friendlysb.append(", ").append(ko.getName());
            }
			else if ( relations.get(ko) == KingdomRelation.ENEMY) {
			    enemysb.append(", ").append(ko.getName());
            }
		}

		// friendlies
        String friendlies = friendlysb.toString();
		if ( friendlies.equals("") ) {
		    friendlies = "/";
        } else {
		    friendlies = friendlies.substring(2);
        }
		if ( !plugin.getMsg().isEmpty("cmdInfoFriendly") ) sender.sendMessage(ChatColor.GRAY + plugin.getMsg().getMessage("cmdInfoFriendly") + ": " + ChatColor.GREEN + friendlies);

		// enemys
        String enemies = enemysb.toString();
		if ( enemies.equals("") ) {
		    enemies = "/";
        } else {
		    enemies = enemies.substring(2);
        }
		if ( !plugin.getMsg().isEmpty("cmdInfoEnemy") ) sender.sendMessage(ChatColor.GRAY + plugin.getMsg().getMessage("cmdInfoEnemy") + ": " + ChatColor.RED + enemies);

		// members
		StringBuilder sb = new StringBuilder();
		String result;

		List<Player> onlineMembers = plugin.getApi().getKingdomHandler().getOnlineMembers(kingdom);
		if ( onlineMembers.size() > 0 ) {
			for ( Player p : onlineMembers ) {
				sb.append(", ").append(p.getName());
			}
			result = sb.toString().substring(2);
		} else {
			result = "/";
		}
		sender.sendMessage(ChatColor.GRAY + "Online Members (" + ChatColor.GOLD + onlineMembers.size() + ChatColor.GRAY + "): " + ChatColor.GREEN + result);

		// offline members
		if ( plugin.getCfg().getBoolean("info-offline-members") ) {

			sb = new StringBuilder();
			List<KingdomUser> offlineMembers = plugin.getApi().getKingdomHandler().getMembers(kingdom).stream()
					.filter(user -> user.getPlayer() == null).collect(Collectors.toList());

			if ( offlineMembers.size() > 0 ) {
				for ( KingdomUser u : offlineMembers ) {
					sb.append(", ").append(u.getName());
				}
				result = sb.toString().substring(2);
			} else {
				result = "/";
			}
			sender.sendMessage(ChatColor.GRAY + "Offline Members (" + ChatColor.GOLD + offlineMembers.size() + ChatColor.GRAY + "): "+ ChatColor.RED + result);
		}
	}
	
	protected void showPlayerInfo(CommandSender sender, Player player) {
		KingdomUser user = plugin.getApi().getUserHandler().getUser(player);
		
		if ( !plugin.getMsg().isEmpty("cmdInfoPlayer") ) {
			plugin.getMsg().send(sender, "cmdInfoPlayer", player.getName());
		}
		
		if ( user.getKingdom() != null ) {
			sender.sendMessage(ChatColor.GRAY + "Kingdom: " + ChatColor.GOLD + plugin.getApi().getUserHandler().getKingdom(user).getDisplay());

			if ( user.getRank() != null ) {
				sender.sendMessage(ChatColor.GRAY + "Rank: " + ChatColor.GOLD + plugin.getApi().getUserHandler().getRank(user).getDisplay());
			}
		} else {
			sender.sendMessage(ChatColor.GRAY + "Kingdom: " + ChatColor.GOLD + "/");
		}
	}
}
