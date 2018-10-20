package com.igufguf.kingdomcraft.commands.members;

import com.igufguf.kingdomcraft.api.models.commands.CommandBase;
import com.igufguf.kingdomcraft.KingdomCraft;
import com.igufguf.kingdomcraft.api.models.kingdom.KingdomUser;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

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
public class InviteCommand extends CommandBase {

	private final KingdomCraft plugin;

	public InviteCommand(KingdomCraft plugin) {
		super("invite", "kingdom.invite", true, "<player>");

		this.plugin = plugin;
	}
	
	@Override
	public List<String> tabcomplete(CommandSender sender, String[] args) {
		return Bukkit.getOnlinePlayers().stream().filter(p -> p != sender).map(HumanEntity::getName).collect(Collectors.toList());
	}
	
	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Player p = (Player) sender;
		
		if ( args.length != 1 ) {
			plugin.getMsg().send(sender, "cmdDefaultUsage");
			return false;
		}
		
		if ( plugin.getApi().getUserHandler().getUser(p).getKingdom() == null ) {
			plugin.getMsg().send(sender, "cmdDefaultSenderNoKingdom");
			return true;
		}
		
		String username = args[0];
		KingdomUser user = plugin.getApi().getUserHandler().getUser(username);
		
		if ( user == null ) {
			plugin.getMsg().send(sender, "cmdDefaultNoPlayer", username);
			return true;
		}

		if ( user.getKingdom() != null && user.getKingdom().equals(plugin.getApi().getUserHandler().getUser(p).getKingdom()) ) {
			plugin.getMsg().send(sender, "cmdInviteAlready", user.getName());
			return true;
		}


		String kingdom = plugin.getApi().getUserHandler().getUser(p).getKingdom();
		if ( user.hasKingdomInvite(kingdom) ) {
			plugin.getMsg().send(sender, "cmdInviteAlready", user.getName());
			return true;
		}

		user.addKingdomInvite(kingdom);

		if ( user.getPlayer() != null ) {
			plugin.getMsg().send(user.getPlayer(), "cmdInviteTarget", kingdom);
		} else {
			plugin.getApi().getUserHandler().save(user);
		}

		plugin.getMsg().send(sender, "cmdInviteSender", user.getName(), kingdom);

		return true;
	}
	
}
