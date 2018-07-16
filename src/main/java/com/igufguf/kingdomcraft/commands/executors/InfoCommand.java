package com.igufguf.kingdomcraft.commands.executors;

import com.igufguf.kingdomcraft.commands.CommandBase;
import com.igufguf.kingdomcraft.commands.CommandHandler;
import com.igufguf.kingdomcraft.KingdomCraft;
import com.igufguf.kingdomcraft.objects.KingdomObject;
import com.igufguf.kingdomcraft.objects.KingdomRelation;
import com.igufguf.kingdomcraft.objects.KingdomUser;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

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
		super("info", null, false);
		addAliasses("i");

		this.plugin = plugin;
		
		plugin.getCmdHandler().register(this);
	}
	
	@Override
	public ArrayList<String> tabcomplete(String[] args) {
		if ( args.length == 2 ) {
			ArrayList<String> kingdoms = new ArrayList<>();
			for ( KingdomObject kd : plugin.getApi().getKingdomManager().getKingdoms() ) {
				if ( kd.getName().toLowerCase().startsWith(args[1].toLowerCase()) ) kingdoms.add(kd.getName());
			}
			for ( Player p : Bukkit.getOnlinePlayers() ) {
				if ( p.getName().toLowerCase().startsWith(args[1].toLowerCase()) ) kingdoms.add(p.getName());
			}
			return kingdoms;
		}
		return null;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean execute(CommandSender sender, String[] args) {
		
		if ( args.length > 1 || (args.length == 0 && !(sender instanceof Player))) {
			plugin.getMsg().send(sender, "cmdDefaultUsage");
			return false;
		}
		if ( args.length == 1 && plugin.getApi().getKingdomManager().getKingdom(args[0]) == null ) {
			if ( Bukkit.getPlayerExact(args[0]) != null ) {
				Player target = Bukkit.getPlayer(args[0]);
				
				showPlayerInfo(sender, target);
				return false;
			}
			plugin.getMsg().send(sender, "cmdDefaultKingdomNotExist", args[0]);
			return false;
		}
		KingdomObject kingdom;
		
		if ( args.length == 1 ) {
			kingdom = plugin.getApi().getKingdomManager().getKingdom(args[0]);
		} else {
			KingdomUser user = plugin.getApi().getUserManager().getUser((Player) sender);
			kingdom = plugin.getApi().getUserManager().getKingdom(user);
		}
		
		if ( kingdom == null ) {
			plugin.getMsg().send(sender, "cmdDefaultTargetNoKingdom");
			return false;
		}
		
		showKingdomInfo(sender, kingdom);
		
		return false;
	}
	
	private void showKingdomInfo(CommandSender sender, KingdomObject kingdom) {
		plugin.getMsg().send(sender, "cmdInfoKingdom", kingdom.getName());
		
		if ( kingdom.hasData("prefix") ) {
			String prefix = ChatColor.translateAlternateColorCodes('&', (String) kingdom.getData("prefix"));
			sender.sendMessage(ChatColor.GRAY + "Chat Prefix: " + prefix + (ChatColor.stripColor(prefix).equals("") ? "abc" : ""));
		}

		String allys = "";
		String enemys = "";

		HashMap<KingdomObject, KingdomRelation> relations = plugin.getApi().getRelationManager().getRelations(kingdom);
		for ( KingdomObject ko : relations.keySet() ) {
			if ( relations.get(ko) == KingdomRelation.FRIENDLY ) allys += ", " + ko.getName();
			else if ( relations.get(ko) == KingdomRelation.ENEMY) enemys += ", " + ko.getName();
		}

		allys = allys.replaceFirst(", ", ""); if ( allys.equals("") ) allys = "none";
		if ( !plugin.getMsg().isEmpty("cmdInfoFriendly") ) sender.sendMessage(ChatColor.GRAY + plugin.getMsg().getMessage("cmdInfoFriendly") + ": " + ChatColor.GREEN + allys);
		
		enemys = enemys.replaceFirst(", ", ""); if ( enemys.equals("") ) enemys = "none";
		if ( !plugin.getMsg().isEmpty("cmdInfoEnemy") ) sender.sendMessage(ChatColor.GRAY + plugin.getMsg().getMessage("cmdInfoEnemy") + ": " + ChatColor.RED + enemys);
		
		String members = "";
		for ( Player p : plugin.getApi().getKingdomManager().getOnlineMembers(kingdom) ) {
			members += ", " + p.getName();
		}
		members = members.replaceFirst(", ", ""); if ( members.equals("") ) members = "none";
		sender.sendMessage(ChatColor.GRAY + "Online Members: " + ChatColor.GREEN + members);

		if ( plugin.getCfg().getBoolean("info-offline-players") ) {
			members = "";
			for (KingdomUser p : plugin.getApi().getKingdomManager().getMembers(kingdom)) {
				if (p.getPlayer() != null) continue;
				members += ", " + p.getName();
			}
			members = members.replaceFirst(", ", "");
			if (members.equals("")) members = "none";
			sender.sendMessage(ChatColor.GRAY + "Offline Members: " + ChatColor.RED + members);
		}
	}
	
	private void showPlayerInfo(CommandSender sender, Player player) {
		KingdomUser user = plugin.getApi().getUserManager().getUser(player);
		
		if ( !plugin.getMsg().isEmpty("cmdInfoPlayer") ) {
			plugin.getMsg().send(sender, "cmdInfoPlayer", player.getName());
		}
		
		if ( user.getKingdom() != null ) sender.sendMessage(ChatColor.GRAY + "Kingdom: " + ChatColor.GOLD + plugin.getApi().getUserManager().getKingdom(user).getDisplay());
		else sender.sendMessage(ChatColor.GRAY + "Kingdom: " + ChatColor.GOLD + "none");
		
		if ( user.getKingdom() != null && user.getRank() != null ) sender.sendMessage(ChatColor.GRAY + "Rank: " + ChatColor.GOLD + user.getRank());
		
		sender.sendMessage(" ");
	}
}
