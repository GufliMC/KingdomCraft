package com.igufguf.kingdomcraft.commands.executors.admin;

import com.igufguf.kingdomcraft.KingdomCraft;
import com.igufguf.kingdomcraft.commands.CommandBase;
import com.igufguf.kingdomcraft.commands.CommandHandler;
import com.igufguf.kingdomcraft.objects.KingdomObject;
import com.igufguf.kingdomcraft.objects.KingdomUser;
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
public class KickCommand extends CommandBase {

	private final KingdomCraft plugin;

	public KickCommand(KingdomCraft plugin) {
		super("kick", "kingdom.kick", true);

		this.plugin = plugin;

		plugin.getCmdHandler().register(this);
	}
	
	@Override
	public List<String> tabcomplete(CommandSender sender, String[] args) {
		if ( sender.hasPermission("kingdom.kick.other") ) {
			return Bukkit.getOnlinePlayers().stream().filter(p -> p != sender).filter(p -> p.getName().startsWith(args[0])).map(HumanEntity::getName).collect(Collectors.toList());
		}

		KingdomUser user = plugin.getApi().getUserManager().getUser((Player) sender);
		KingdomObject ko = plugin.getApi().getUserManager().getKingdom(user);
		if ( ko == null ) return null;

		return plugin.getApi().getKingdomManager().getOnlineMembers(ko).stream().filter(p -> p != sender).filter(p -> p.getName().startsWith(args[0])).map(HumanEntity::getName).collect(Collectors.toList());
	}
	
	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Player p = (Player) sender;
		
		if ( args.length != 1 ) {
			plugin.getMsg().send(sender, "cmdDefaultUsage");
			return false;
		}
		String username = args[0];
		KingdomUser user = plugin.getApi().getUserManager().getUser(username);
		
		if ( user == null ) {
			plugin.getMsg().send(sender, "cmdDefaultNoPlayer", username);
			return false;
		}
		if ( user.getKingdom() == null ) {
			plugin.getMsg().send(sender, "cmdDefaultTargetNoKingdom", user.getName());
			return false;
		}
		
		if ( p.hasPermission(this.permission + ".other") || (plugin.getApi().getUserManager().getUser(p) != null && plugin.getApi().getUserManager().getUser(p).getKingdom() == user.getKingdom()) ) {
			KingdomObject kingdom = plugin.getApi().getUserManager().getKingdom(user);

			plugin.getApi().getUserManager().setKingdom(user, null);

			for ( Player member : plugin.getApi().getKingdomManager().getOnlineMembers(kingdom) ) {
				plugin.getMsg().send(member, "cmdLeaveSuccessMembers", user.getName());
			}

			plugin.getMsg().send(sender, "cmdKickSender", user.getName(), kingdom.getName());

			if ( user.getPlayer() != null ) {
				plugin.getMsg().send(user.getPlayer(), "cmdKickTarget", kingdom.getName());
			}
			
			if ( plugin.getCfg().getBoolean("spawn-on-kingdom-leave") && user.getPlayer() != null ) {
				Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "spawn " + user.getName());
			}
		} else {
			plugin.getMsg().send(sender, "noPermissionCmd");
		}
		
		//save user when player is not online
		if ( user.getPlayer() == null ) {
			plugin.getApi().getUserManager().save(user);
		}
		
		return true;
	}
}
