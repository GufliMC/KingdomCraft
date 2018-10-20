package com.igufguf.kingdomcraft.commands.players;

import com.igufguf.kingdomcraft.KingdomCraft;
import com.igufguf.kingdomcraft.api.models.commands.CommandBase;
import com.igufguf.kingdomcraft.managers.ChatManager;
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
public class ChannelCommand extends CommandBase {

	private final KingdomCraft plugin;
	private final ChatManager cm;

	public ChannelCommand(KingdomCraft plugin) {
		super("channel", "kingdom.channel", true, "<channel>");
		addAliasses("ch", "c");

		this.plugin = plugin;
		this.cm = plugin.getChatManager();
	}

	@Override
	public List<String> tabcomplete(CommandSender sender, String[] args) {
		if ( args.length == 1 ) {
			List<String> channels = new ArrayList<>();
			for ( ChatManager.Channel c : cm.getChannels() ) {
				if ( c.getName().toLowerCase().startsWith(args[0].toLowerCase()) ) {
					if ( c.isPermission() && !sender.hasPermission(this.getPermission() + "." + c.getName())) continue;
					channels.add(c.getName());
				}
			}
			return channels;
		}
		return null;
	}
	
	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Player p = (Player) sender;
		
		if ( args.length != 1 ) {
			return false;
		}


		ChatManager.Channel channel = null;

		for ( ChatManager.Channel c : cm.getChannels() ) {
			if ( c.getName().equalsIgnoreCase(args[0]) ) {
				channel = c;
			}
		}

		if ( channel == null ) {
			plugin.getMsg().send(p, "cmdChannelNotExist", args[0]);
			return true;
		}

		if ( channel.isPermission() && !p.hasPermission(this.getPermission() + "." + channel.getName()) ) {
			plugin.getMsg().send(p, "cmdChannelNoPerm", channel.getName());
			return true;
		}

		if ( channel.isAlwaysEnabled() ) {
			plugin.getMsg().send(p, "cmdChannelToggle", channel.getName());
			return true;
		}

		KingdomUser user = plugin.getApi().getUserHandler().getUser(p);
		boolean enabled = user.isChannelEnabled(channel.getName());

		if ( enabled ) {
			user.setChannelEnabled(channel.getName(), false);
			plugin.getMsg().send(p, "cmdChannelDisable", channel.getName());
		} else {
			user.setChannelEnabled(channel.getName(), true);
			plugin.getMsg().send(p, "cmdChannelEnable", channel.getName());
		}

		return true;
	}

}
