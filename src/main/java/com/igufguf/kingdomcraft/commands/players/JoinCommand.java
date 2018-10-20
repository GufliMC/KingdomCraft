package com.igufguf.kingdomcraft.commands.players;

import com.igufguf.kingdomcraft.KingdomCraft;
import com.igufguf.kingdomcraft.api.models.commands.CommandBase;
import com.igufguf.kingdomcraft.api.models.flags.KingdomFlag;
import com.igufguf.kingdomcraft.api.models.kingdom.Kingdom;
import com.igufguf.kingdomcraft.api.models.kingdom.KingdomUser;
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
		super("join", "kingdom.join", true, "<kingdom>");

		this.plugin = plugin;
	}
	
	@Override
	public List<String> tabcomplete(CommandSender sender, String[] args) {
		if ( args.length == 1 ) {
			KingdomUser user = plugin.getApi().getUserHandler().getUser((Player) sender);

			List<String> kingdoms = new ArrayList<>();
			for ( Kingdom kd : plugin.getApi().getKingdomHandler().getKingdoms() ) {
				if ( kd.getName().toLowerCase().startsWith(args[0].toLowerCase()) ) {
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
			return false;
		}
		if ( plugin.getApi().getKingdomHandler().getKingdom(args[0]) == null ) {
			plugin.getMsg().send(sender, "cmdDefaultKingdomNotExist", args[0]);
			return true;
		}
		if ( plugin.getApi().getUserHandler().getUser(p).getKingdom() != null ) {
			plugin.getMsg().send(sender, "cmdJoinAlready");
			return true;
		}
		
		Kingdom kingdom = plugin.getApi().getKingdomHandler().getKingdom(args[0]);
		KingdomUser user = plugin.getApi().getUserHandler().getUser(p);
		
		if ( plugin.getApi().getFlagHandler().hasFlag(kingdom, KingdomFlag.INVITE_ONLY)
				&& plugin.getApi().getFlagHandler().getFlag(kingdom, KingdomFlag.INVITE_ONLY)
				&& !user.hasKingdomInvite(kingdom.getName()) && !p.hasPermission("kingdom.join." + kingdom.getName()) ) {

			plugin.getMsg().send(sender, "cmdJoinInviteOnly", kingdom.getName());

			for ( Player member : plugin.getApi().getKingdomHandler().getOnlineMembers(kingdom) ) {
				plugin.getMsg().send(member, "cmdJoinInviteOnlyMembers", p.getName());
			}

			return true;
		}

		if ( plugin.getApi().getKingdomHandler().getMembers(kingdom).size() >= kingdom.getMaxMembers() ) {
			plugin.getMsg().send(sender, "cmdJoinFull", kingdom.getName());
			return true;
		}

		for ( Player member : plugin.getApi().getKingdomHandler().getOnlineMembers(kingdom) ) {
			plugin.getMsg().send(member, "cmdJoinSuccessMembers", p.getName());
		}

		plugin.getApi().getUserHandler().setKingdom(user, kingdom);

		plugin.getMsg().send(sender, "cmdJoinSuccess", kingdom.getName());

		if ( kingdom.getSpawn() != null && plugin.getCfg().getBoolean("spawn-on-kingdom-join") ) {
			p.teleport(kingdom.getSpawn());
		}

		return true;
	}

}
