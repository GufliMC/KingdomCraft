package com.igufguf.kingdomcraft.commands.executors;

import com.igufguf.kingdomcraft.KingdomCraft;
import com.igufguf.kingdomcraft.commands.CommandBase;
import com.igufguf.kingdomcraft.commands.CommandHandler;
import com.igufguf.kingdomcraft.listeners.ChatListener;
import com.igufguf.kingdomcraft.managers.ChatManager;
import com.igufguf.kingdomcraft.objects.KingdomUser;
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
public class ChannelCommand extends CommandBase {

	private final KingdomCraft plugin;
	private final ChatManager cm;

	public ChannelCommand(KingdomCraft plugin) {
		super("channel", "kingdom.channel", true);
		addAliasses("ch", "c");

		this.plugin = plugin;
		this.cm = plugin.getApi().getChatManager();

		if ( !cm.isChatSystemEnabled() || !cm.areChannelsEnabled() ) {
			return;
		}

		plugin.getCmdHandler().register(this);
	}

	@Override
	public ArrayList<String> tabcomplete(String[] args) {
		if ( args.length == 2 ) {
			ArrayList<String> channels = new ArrayList<>();
			for ( ChatManager.Channel c : cm.getChannels() ) {
				if ( c.getName().toLowerCase().startsWith(args[1].toLowerCase()) ) channels.add(c.getName());
			}
			return channels;
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


		ChatManager.Channel channel = null;

		for ( ChatManager.Channel c : cm.getChannels() ) {
			if ( c.getName().equalsIgnoreCase(args[0]) ) {
				channel = c;
			}
		}

		if ( channel == null ) {
			plugin.getMsg().send(p, "cmdChannelNotExist", args[0]);
			return false;
		}

		if ( channel.isPermission() && !p.hasPermission("kingdom.channel." + channel.getName()) ) {
			plugin.getMsg().send(p, "cmdChannelNoPerm", channel.getName());
			return false;
		}

		if ( channel.isAlwayson() || (cm.getDefaultChannel() != null && cm.getDefaultChannel().equals(channel.getName())) ) {
			plugin.getMsg().send(p, "cmdChannelToggle", channel.getName());
			return false;
		}

		KingdomUser user = plugin.getApi().getUserManager().getUser(p);
		if ( !user.hasInList("channels", channel.getName()) ) {
			user.addInList("channels", channel.getName());
			plugin.getMsg().send(p, "cmdChannelEnable", channel.getName());
		} else {
			user.delInList("channels", channel.getName());
			plugin.getMsg().send(p, "cmdChannelDisable", channel.getName());
		}

		return false;
	}

}
