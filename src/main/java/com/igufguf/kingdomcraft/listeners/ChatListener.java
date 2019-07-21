package com.igufguf.kingdomcraft.listeners;

import com.igufguf.kingdomcraft.KingdomCraft;
import com.igufguf.kingdomcraft.managers.ChatManager;
import com.igufguf.kingdomcraft.api.models.kingdom.Kingdom;
import com.igufguf.kingdomcraft.api.models.kingdom.KingdomRank;
import com.igufguf.kingdomcraft.api.events.AsyncKingdomChatEvent;
import com.igufguf.kingdomcraft.api.models.kingdom.KingdomUser;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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
public class ChatListener extends EventListener {

	private final ChatManager cm;

	public ChatListener(KingdomCraft plugin) {
		super(plugin);
		this.cm = plugin.getChatManager();
	}

	@EventHandler(ignoreCancelled=true, priority=EventPriority.HIGHEST)
	public void onChat(AsyncPlayerChatEvent e) {
		if ( !isWorldEnabled(e.getPlayer().getWorld()) ) return;
		if ( !cm.isChatSystemEnabled() ) return;

		Player p = e.getPlayer();
		KingdomUser user = plugin.getApi().getUserHandler().getUser(p);

		String message = e.getMessage();

		// anti caps
		if ( cm.isAntiCapsEnabled() ) {
			if ( isCaps(message) && !p.hasPermission("kingdom.chat.bypass.caps") && !p.hasPermission("kingdom.chat.caps.bypass")) {
				message = message.toLowerCase();
			}
		}

		// anti advertise
		boolean advertise = false;
		if ( cm.isAntiAdvertiseEnabled() ) {
			if ( isAdvertising(message) && !p.hasPermission("kingdom.chat.bypass.advertise") && !p.hasPermission("kingdom.chat.advertise.bypass")) {
				advertise = true;
			}
		}

		String format;
		List<Player> receivers = new ArrayList<>();

		if ( !cm.areChannelsEnabled() ) {
			format = cm.getDefaultFormat();

			// format is null? well thats not good
			if ( format == null ) return;

			for ( Player online : Bukkit.getOnlinePlayers() ) {
				if ( online != p && isWorldEnabled(online.getWorld()) ) {
					receivers.add(online);
				}
			}
		}
		else {
			ChatManager.Channel channel = null;

			// check which channel he is talking in
			for ( ChatManager.Channel c : cm.getChannels() ) {
				if ( ( user.isChannelEnabled(c.getName()) || c.isAlwaysEnabled()) // enabled
						&& c.getMessagePrefix() != null // messag prefix is not null
						&& ChatColor.stripColor(message).startsWith(c.getMessagePrefix()) //message prefix matches sent message
						&& (channel == null || channel.getMessagePrefix().length() < c.getMessagePrefix().length()) ) { // only when no channel is found or a better matching prefix is found
					channel = c;
				}
			}

			// if no channel is detected or player has no permission for this channel, talk in default channel
			if ( channel == null || (channel.isPermission() && !p.hasPermission("kingdom.channel." + channel.getName()) && !p.isOp() )) {
				channel = cm.getChannel(cm.getDefaultChannel(user));

				// player is not in a kingdom and there is no default channel for kingdomless players
				if ( channel == null && user.getKingdom() == null  ) {
					plugin.getMsg().send(p, "chatNoKingdom");
					e.setCancelled(true);
					return;
				}

				// no default channel was found
				if ( channel == null ) return;
			}

			format = channel.getFormat();

			// format is null? well thats not good
			if ( format == null ) return;

			// remove the message prefix from the message
			if ( channel.getMessagePrefix() != null ) {
				message = message.replaceFirst(Pattern.quote(channel.getMessagePrefix()), "");
			}

			// set the receivers
			for ( KingdomUser u : plugin.getApi().getUserHandler().getUsers() ) {
				if ( u == user ) continue;
				Player up = u.getPlayer();

				if ( !up.isOnline() ) continue;
				if ( !isWorldEnabled(up.getWorld()) ) continue;
				if ( channel.isPermission() && !up.hasPermission("kingdom.channel." + channel.getName()) && !up.isOp() ) continue;
				if ( !u.isChannelEnabled(channel.getName()) && !channel.isAlwaysEnabled() ) continue;

				if ( channel.getVisibilityType() == ChatManager.VisibilityType.KINGDOM ) {
					// player must be in same kingdom for this visibility
					if ( u.getKingdom() != null && u.getKingdom().equals(user.getKingdom()) ) {
						receivers.add(up);
					}
				} else {
					receivers.add(up);
				}
			}
		}

		// cancel original chat event
		e.setCancelled(true);

		// remove leading & trailing spaces
		message = message.trim();

		// check if people dont send empty messages by entering a color code
		String rawmessage = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', message));
		if ( rawmessage.replaceAll(" ", "").equals("") ) return;

		// if message is advertise, fool player that msg is sent and alert staff
		if ( advertise ) {
			p.sendMessage(format);

			for ( Player on : Bukkit.getOnlinePlayers() ) {
				if ( on.hasPermission("kingdom.chat.advertise.notify") ) {
					plugin.getMsg().send(on, "chatAdvertising", p.getName(), rawmessage);
				}
			}
			return;
		}

		// call chat event
		AsyncKingdomChatEvent event = new AsyncKingdomChatEvent(p, format, message, receivers, !Bukkit.getServer().isPrimaryThread());
		Bukkit.getServer().getPluginManager().callEvent(event);

		if ( event.isCancelled() ) return;

		String newprefix = user.getKingdom() == null && plugin.getCfg().has("default-prefix") ? plugin.getCfg().getString("default-prefix") : "";
		newprefix = ChatColor.translateAlternateColorCodes('&', newprefix);

		// do the formatting & replace variables
		format = event.getFormat();
		format = format.replace("{player}", newprefix + p.getDisplayName());
		format = format.replace("{username}",  newprefix + p.getName());
		format = ChatColor.translateAlternateColorCodes('&', format);

		message = event.getMessage();

		// only translate chat colors if player has permission
		if ( p.hasPermission("kingdom.chat.colors") ) {
			message = ChatColor.translateAlternateColorCodes('&', message);
		}

		format = format.replace("{message}", message);

		// send message to self
		p.sendMessage(format);

		// send message to receivers
		for ( KingdomUser u : plugin.getApi().getUserHandler().getUsers() ) {
			if ( u == user) continue;

			Player up = u.getPlayer();
			if ( receivers.contains(up) ) {
				up.sendMessage(format);
			} else if ( u.isSocialSpyEnabled() && up.hasPermission("kingdom.socialspy") ) {
				up.sendMessage(ChatColor.WHITE + "SS > " + format);
			}
		}

		System.out.println(ChatColor.stripColor(format));
	}

	// listen to kingdom chat event, replace variables with corresponding values
	@EventHandler
	public void onKingdomChat(AsyncKingdomChatEvent e) {
		String format = e.getFormat();
		Player p = e.getPlayer();
		KingdomUser user = plugin.getApi().getUserHandler().getUser(p);

		String[] placeholders = {
				"kingdom", "kingdomprefix", "kingdomsuffix",
				"kingdomrank", "kingdomrankprefix", "kingdomranksuffix",
				"prefix", "suffix"};

		Map<String, String> values = new HashMap<>();

		Kingdom kd = plugin.getApi().getUserHandler().getKingdom(user);
		if ( kd != null ) {
			values.put("kingdom", kd.getDisplay());
			values.put("kingdomname", kd.getName());

			values.put("kingdomprefix", kd.getPrefix());
			values.put("kingdomsuffix", kd.getSuffix());

			KingdomRank rank = plugin.getApi().getUserHandler().getRank(user);
			if ( rank != null ) {
				values.put("kingdomrank", formatColors(rank.getDisplay()));
				values.put("kingdomrankname", formatColors(rank.getName()));

				values.put("kingdomrankprefix", rank.getPrefix());
				values.put("kingdomranksuffix", rank.getSuffix());
			}
		}

		if ( cm.hasVault() ) {
			if ( cm.getVault().getPlayerPrefix(p) != null ) values.put("prefix", cm.getVault().getPlayerPrefix(p));
			if ( cm.getVault().getPlayerSuffix(p) != null ) values.put("suffix", cm.getVault().getPlayerSuffix(p));
		}

		for ( String placeholder : placeholders ) {
			if ( !format.contains("{" + placeholder + "}") ) continue;

			if ( !values.containsKey(placeholder) || values.get(placeholder) == null ) {
				format = format.replace("{" + placeholder + "}", "");
			} else {
				format = format.replace("{" + placeholder + "}", values.get(placeholder));
			}
		}

		e.setFormat(format);
	}

	// CHAT MANAGEMENT

	private String formatColors(String msg) {
		return ChatColor.translateAlternateColorCodes('&', msg);
	}

	private boolean isCaps(String message) {
		List<String> list = Arrays.asList("A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z");
		
		int caps = 0;
		for ( String key : message.split("") ) {
			if ( list.contains(key) ) caps++;
		}
		
		if ( caps >= 3 && caps >= ((double) message.length())/3.0 ) {
			return true;
		}
		return false;
	}
	
	private final Pattern ipv4Pattern = Pattern.compile("^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$", Pattern.CASE_INSENSITIVE);
	private final Pattern ipv6Pattern = Pattern.compile("([0-9a-f]{1,4}:){7}([0-9a-f]){1,4}", Pattern.CASE_INSENSITIVE);
	private final Pattern webpattern = Pattern.compile("[-a-zA-Z0-9@:%_\\+.~#?&//=]{2,256}\\.[a-z]{2,4}\\b(\\/[-a-zA-Z0-9@:%_\\+~#?&//=]*)?");
	
	private boolean isAdvertising(String message) {
		
	    String str = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', message.toLowerCase()));

	    Matcher regexMatcher = ipv4Pattern.matcher(str);
	    while (regexMatcher.find()) {
	    	if ((regexMatcher.group().length() != 0) && (ipv4Pattern.matcher(str).find())) {
	    		return true;
	    	}
	    }

	    regexMatcher = ipv6Pattern.matcher(str);
	    while (regexMatcher.find()) {
	    	if ((regexMatcher.group().length() != 0) && (ipv6Pattern.matcher(str).find())) {
	    		return true;
	    	}
	    }
	    
	    Matcher regexMatcherurl = webpattern.matcher(message);
	    while (regexMatcherurl.find()) {
	    	String text = regexMatcherurl.group().trim().replaceAll("www.", "").replaceAll("http://", "").replaceAll("https://", "");
	    	if ((regexMatcherurl.group().length() != 0) && (text.length() != 0)) {
	    		if (webpattern.matcher(message).find()) {
	    			return true;
	    		}
	    	}
	    }

	    return false;
	}


}
