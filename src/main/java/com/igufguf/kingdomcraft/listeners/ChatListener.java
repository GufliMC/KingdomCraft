package com.igufguf.kingdomcraft.listeners;

import com.igufguf.kingdomcraft.KingdomCraft;
import com.igufguf.kingdomcraft.managers.ChatManager;
import com.igufguf.kingdomcraft.objects.KingdomObject;
import com.igufguf.kingdomcraft.objects.KingdomRank;
import com.igufguf.kingdomcraft.events.KingdomChatEvent;
import com.igufguf.kingdomcraft.objects.KingdomUser;
import com.igufguf.kingdomcraft.KingdomCraftConfig;
import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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
public class ChatListener extends EventListener {

	private final ChatManager cm;

	public ChatListener(KingdomCraft plugin) {
		super(plugin);
		this.cm = plugin.getApi().getChatManager();
	}


	@EventHandler(ignoreCancelled=true, priority=EventPriority.HIGHEST)
	public void onChat(AsyncPlayerChatEvent e) {
		if ( !isWorldEnabled(e.getPlayer().getWorld()) ) return;

		Player p = e.getPlayer();
		KingdomUser user = plugin.getApi().getUserManager().getUser(p);

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
				if ( ( user.hasInList("channels", c.getName()) || c.isAlwayson() )
						&& c.getMessagePrefix() != null
						&& ChatColor.stripColor(message).startsWith(c.getMessagePrefix())
						&& ( channel == null || channel.getMessagePrefix().length() < c.getMessagePrefix().length() ) ) {
					channel = c;
				}
			}

			// if no channel is detected, he is talking in the default channel
			if ( channel == null ) {
				channel = cm.getChannel(cm.getDefaultChannel());
				if ( channel == null ) return;
			} else {

				// if player has no permission for this channel, talk in default channel
				if (channel.isPermission() && !p.hasPermission("kingdom.channel." + channel.getName()) && !p.isOp()) {
					channel = cm.getChannel(cm.getDefaultChannel());
					if (channel == null) return;
				}
			}

			// if the type is not public and he is not in a kingdom, he cant talk there so fallback to no kingdom channel
			if ( channel.getVisibilityType() != ChatManager.VisibilityType.PUBLIC && user.getKingdom() == null ) {
				channel = cm.getChannel(cm.getNoKingdomChannel());

				// if channel is null or type is not public (which is weird) alert player that he cant talk
				if ( channel == null || channel.getVisibilityType() != ChatManager.VisibilityType.PUBLIC ) {
					plugin.getMsg().send(p, "chatNoKingdom");
					e.setCancelled(true);
					return;
				}
			}

			format = channel.getFormat();

			// format is null? well thats not good
			if ( format == null ) return;

			// remove the message prefix from the message
			if ( channel.getMessagePrefix() != null ) {
				message = message.replaceFirst(Pattern.quote(channel.getMessagePrefix()), "");
			}

			// set the receivers
			for ( KingdomUser u : plugin.getApi().getUserManager().getUsers() ) {
				if ( u == user ) continue;
				Player up = u.getPlayer();

				if ( !up.isOnline() ) continue;
				if ( !isWorldEnabled(up.getWorld()) ) continue;
				if ( channel.isPermission() && !up.hasPermission("kingdom.channel." + channel.getName()) && !up.isOp() ) continue;
				if ( !u.hasInList("channels", channel.getName()) && !channel.isAlwayson() && (cm.getDefaultChannel() == null || !channel.getName().equalsIgnoreCase(cm.getDefaultChannel()))) continue;

				if ( channel.getVisibilityType() == ChatManager.VisibilityType.KINGDOM ) {
					// player must be in same kingdom for this visibility
					if ( u.getKingdom() != null && u.getKingdom() == user.getKingdom() ) {
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
		KingdomChatEvent event = new KingdomChatEvent(p, format, message, receivers);
		Bukkit.getServer().getPluginManager().callEvent(event);

		if ( event.isCancelled() ) return;

		String newprefix = user.getKingdom() == null && plugin.getCfg().has("default-prefix") ? plugin.getCfg().getString("default-prefix") : "";
		newprefix = ChatColor.translateAlternateColorCodes('&', newprefix);

		// do the formatting & replace variables
		format = event.getFormat();
		format = format.replace("{NAME}", newprefix + p.getDisplayName());
		format = format.replace("{USERNAME}",  newprefix + p.getName());
		format = ChatColor.translateAlternateColorCodes('&', format);

		message = event.getMessage();

		// only translate chat colors if player has permission
		if ( p.hasPermission("kingdom.chat.colors") ) {
			message = ChatColor.translateAlternateColorCodes('&', message);
		}

		format = format.replace("{MESSAGE}", message);

		// send message to self
		p.sendMessage(format);

		// send message to receivers
		for ( KingdomUser u : plugin.getApi().getUserManager().getUsers() ) {
			if ( u == user) continue;

			Player up = u.getPlayer();
			if ( receivers.contains(up) ) {
				up.sendMessage(format);
			} else if ( u.hasData("socialspy") && (boolean) u.getData("socialspy") && up.hasPermission("kingdom.socialspy") ) {
				up.sendMessage(ChatColor.WHITE + "SS > " + format);
			}
		}

		System.out.println(ChatColor.stripColor(format));
	}

	// listen to kingdom chat event, replace variables with corresponding values
	@EventHandler
	public void onKingdomChat(KingdomChatEvent e) {
		String format = e.getFormat();
		Player p = e.getPlayer();
		KingdomUser user = plugin.getApi().getUserManager().getUser(p);

		if ( user.getKingdom() != null ) {
			KingdomObject kd = plugin.getApi().getUserManager().getKingdom(user);

			format = format.replace("{KINGDOM_NAME}", kd.getName());

			if ( kd.hasData("prefix") && format.contains("{KINGDOM}")) {
				format = format.replace("{KINGDOM}", ChatColor.translateAlternateColorCodes('&', (String) kd.getData("prefix"))); //The old one
			}
			if ( kd.hasData("prefix") && format.contains("{KINGDOM_PREFIX}")) {
				format = format.replace("{KINGDOM_PREFIX}", ChatColor.translateAlternateColorCodes('&', (String) kd.getData("prefix")));
			}
			if ( kd.hasData("suffix") && format.contains("{KINGDOM_SUFFIX}")) {
				format = format.replace("{KINGDOM_SUFFIX}", ChatColor.translateAlternateColorCodes('&', (String) kd.getData("suffix")));
			}
			if ( kd.hasData("tabprefix") && format.contains("{KINGDOM_TABPREFIX}")) {
				format = format.replace("{KINGDOM_TABPREFIX}", ChatColor.translateAlternateColorCodes('&', (String) kd.getData("tabprefix")));
			}
			if ( kd.hasData("tabsuffix") && format.contains("{KINGDOM_TABSUFFIX}")) {
				format = format.replace("{KINGDOM_TABSUFFIX}", ChatColor.translateAlternateColorCodes('&', (String) kd.getData("tabsuffix")));
			}

			KingdomRank rank = plugin.getApi().getUserManager().getRank(user);
			if ( rank != null ) {
				format = format.replace("{KINGDOMRANK_NAME}", rank.getName());

				if (rank.hasData("prefix") && format.contains("{KINGDOMRANK}")) {
					format = format.replace("{KINGDOMRANK}", ChatColor.translateAlternateColorCodes('&', (String) rank.getData("prefix"))); //The old one
				}
				if (rank.hasData("prefix") && format.contains("{KINGDOMRANK_PREFIX}")) {
					format = format.replace("{KINGDOMRANK_PREFIX}", ChatColor.translateAlternateColorCodes('&', (String) rank.getData("prefix")));
				}
				if (rank.hasData("suffix") && format.contains("{KINGDOMRANK_SUFFIX}")) {
					format = format.replace("{KINGDOMRANK_SUFFIX}", ChatColor.translateAlternateColorCodes('&', (String) rank.getData("suffix")));
				}
				if (rank.hasData("tabprefix") && format.contains("{KINGDOMRANK_TABPREFIX}")) {
					format = format.replace("{KINGDOMRANK_TABPREFIX}", ChatColor.translateAlternateColorCodes('&', (String) rank.getData("tabprefix")));
				}
				if (rank.hasData("tabsuffix") && format.contains("{KINGDOMRANK_TABSUFFIX}")) {
					format = format.replace("{KINGDOMRANK_TABSUFFIX}", ChatColor.translateAlternateColorCodes('&', (String) rank.getData("tabsuffix")));
				}
			}
		}

		if ( cm.hasVault() ) {
			if ( cm.getVault().getPlayerPrefix(p) != null ) {
				format = format.replace("{PREFIX}",  cm.getVault().getPlayerPrefix(p));
			}
			if ( cm.getVault().getPlayerSuffix(p) != null ) {
				format = format.replace("{SUFFIX}", cm.getVault().getPlayerSuffix(p));
			}
		}

		String[] placeholders = {"RANK", "PREFIX", "SUFFIX", "KINGDOM", "KINGDOM_PREFIX", "KINGDOM_SUFFIX", "KINGDOM_TABPREFIX", "KINGDOM_TABSUFFIX",
			"KINGDOMRANK", "KINGDOMRANK_PREFIX", "KINGDOMRANK_SUFFIX", "KINGDOMRANK_TABPREFIX", "KINGDOMRANK_TABSUFFIX"};
		for ( String placeholder : placeholders ) {
			if ( format.contains("{" + placeholder + "}") ) format = format.replace("{" + placeholder + "}", "");
		}

		e.setFormat(format);
	}

	// CHAT MANAGEMENT
	
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
	    str = str.replaceAll("-", ".");
	    str = str.replaceAll(",", ".");
	    str = str.replaceAll("#", ".");
	    str = str.replaceAll(":", ".");
	    str = str.replaceAll(" ", "");
	    while ( str.contains("..") ) str = str.replace("..", ".");
	    
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
