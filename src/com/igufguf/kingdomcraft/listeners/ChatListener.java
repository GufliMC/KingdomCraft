package com.igufguf.kingdomcraft.listeners;

import com.igufguf.kingdomcraft.KingdomCraft;
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
public class ChatListener extends com.igufguf.kingdomcraft.listeners.EventListener {

	public boolean hasvaultchat = false;
	public net.milkbowl.vault.chat.Chat chat;

	private boolean chatsystem = false;

	private boolean antiadvertise = false;
	private boolean anticaps = false;

	private boolean channelsenabled = false;

	private String nokingdomchannel;
	public String defaultchannel;
	private ArrayList<Channel> channels;

	private String defaultformat;

	public ChatListener(KingdomCraft plugin) {
		if ( plugin.getServer().getPluginManager().getPlugin("Vault") != null ) {
	        RegisteredServiceProvider<net.milkbowl.vault.chat.Chat> chatProvider = plugin.getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
	        if (chatProvider != null) {
	            chat = chatProvider.getProvider();
	            hasvaultchat = true;
	        }
		}

		KingdomCraftConfig config = KingdomCraft.getConfg();

		chatsystem = config.getBoolean("chat-system");
		antiadvertise = config.getBoolean("anti-advertise");
		anticaps = config.getBoolean("anti-caps");

		if ( channelsenabled = config.getBoolean("channels.enabled") ) {
			channels = new ArrayList<>();

			for ( String name : config.getSection("channels").getKeys(false) ) {
				if ( name.equalsIgnoreCase("enabled") ) continue;
				if ( !config.has("channels." + name + ".format") ) continue;

				if ( config.getBoolean("channels." + name + ".default") ) {
					defaultchannel = name;
				}

				VisibilityType type = VisibilityType.PUBLIC;

				if ( config.has("channels." + name + ".visibility") ) {

					String s = config.getString("channels." + name + ".visibility");
					if ( s.equalsIgnoreCase("kingdom") ) type = VisibilityType.KINGDOM;
					else if ( s.equalsIgnoreCase("public") ) type = VisibilityType.PUBLIC;

				} else if ( config.getBoolean("channels." + name + ".kingdom-only") ) {
					type = VisibilityType.KINGDOM;
				}

				String format = config.getString("channels." + name + ".format");

				String mprefix = config.getString("channels." + name + ".message-prefix");

				boolean alwayson = config.has("channels." + name + ".alwayson") && config.getBoolean("channels." + name + ".alwayson");
				boolean permission = config.has("channels." + name + ".permission") && config.getBoolean("channels." + name + ".permission");

				Channel c = new Channel(name, format, mprefix, type, alwayson, permission);
				addChannel(c);
			}
		}

		if ( config.has("nokingdom-channel") ) nokingdomchannel = config.getString("nokingdom-channel");

		if ( config.has("defaultformat") ) defaultformat = config.getString("defaultformat");
		else if ( config.has("nochannels-format") ) defaultformat = config.getString("nochannels-format");
	}

	public void addChannel(Channel c) {
		if ( !channels.contains(c) ) channels.add(c);
	}

	@EventHandler(ignoreCancelled=true, priority=EventPriority.HIGHEST)
	public void onChat(AsyncPlayerChatEvent e) {
		if ( !chatsystem ) return;
		if ( !enabledWorld(e.getPlayer().getWorld()) ) return;

		Player p = e.getPlayer();
		KingdomUser user = KingdomCraft.getApi().getUser(p);

		String message = e.getMessage();

		if ( anticaps ) {
			if ( isCaps(message) && !p.hasPermission("kingdom.chat.bypass.caps") && !p.hasPermission("kingdom.chat.caps.bypass")) {
				message = message.toLowerCase();
			}
		}

		boolean advertise = false;
		if ( antiadvertise ) {
			if ( isAdvertising(message) && !p.hasPermission("kingdom.chat.bypass.advertise") && !p.hasPermission("kingdom.chat.advertise.bypass")) {
				advertise = true;
			}
		}

		String format;
		List<Player> receivers = new ArrayList<>();

		if ( channelsenabled ) {
			Channel channel = null;

			for ( Channel c : channels ) {
				if ( ( user.hasInList("channels", c.getName()) || c.isAlwayson() )
						&& c.getMessagePrefix() != null
						&& ChatColor.stripColor(message).startsWith(c.getMessagePrefix())
						&& ( channel == null || channel.getMessagePrefix().length() < c.getMessagePrefix().length() ) ) {
					channel = c;
				}
			}

			if ( channel == null ) {
				channel = getChannel(defaultchannel);
				if ( channel == null ) return;
			}

			if ( channel.isPermission() && !p.hasPermission("kingdom.channel." + channel.getName()) && !p.isOp() ) {
				channel = getChannel(defaultchannel);
				if ( channel == null ) return;
			}

			if ( channel.getVisibilityType() != VisibilityType.PUBLIC && user.getKingdom() == null ) {
				channel = getChannel(nokingdomchannel);
				if ( channel == null || channel.getVisibilityType() != VisibilityType.PUBLIC ) {
					KingdomCraft.getMsg().send(p, "chatNoKingdom");
					e.setCancelled(true);
					return;
				}
			}

			format = channel.getFormat();
			if ( channel.getMessagePrefix() != null ) {
				message = message.replaceFirst(Pattern.quote(channel.getMessagePrefix()), "");
			}

			for ( KingdomUser u : KingdomCraft.getApi().getUsers() ) {
				if ( u == user ) continue;
				Player up = u.getPlayer();

				if ( !up.isOnline() ) continue;
				if ( !enabledWorld(up.getWorld()) ) continue;
				if ( channel.isPermission() && !up.hasPermission("kingdom.channel." + channel.getName()) && !up.isOp() ) continue;
				if ( !u.hasInList("channels", channel.getName()) && !channel.isAlwayson() && (defaultchannel == null || !channel.getName().equalsIgnoreCase(defaultchannel))) continue;

				if ( channel.getVisibilityType() == VisibilityType.KINGDOM ) {
					if ( u.getKingdom() != null && u.getKingdom() == user.getKingdom() ) {
						receivers.add(up);
					}
				} else {
					receivers.add(up);
				}
			}
		} else {
			format = defaultformat;

			for ( Player online : Bukkit.getOnlinePlayers() ) {
				if ( online != p && enabledWorld(online.getWorld()) ) {
					receivers.add(online);
				}
			}
		}

		if ( format == null ) return;
		e.setCancelled(true);

		while ( message.startsWith(" ") ) message = message.replaceFirst(" ", "");

		String rawmessage = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', message));
		if ( rawmessage.replaceAll(" ", "").equals("") ) return;

		if ( advertise ) {
			p.sendMessage(format);
			for ( Player on : Bukkit.getOnlinePlayers() ) {
				if ( on.hasPermission("kingdom.chat.advertise.notify") ) {
					KingdomCraft.getMsg().send(on, "chatAdvertising", p.getName(), rawmessage);
				}
			}
			return;
		}

		KingdomChatEvent event = new KingdomChatEvent(p, format, message, receivers);
		Bukkit.getServer().getPluginManager().callEvent(event);

		if ( event.isCancelled() ) return;

		String newprefix = user.getKingdom() == null && KingdomCraft.getConfg().has("default-prefix") ? KingdomCraft.getConfg().getString("default-prefix") : "";
		newprefix = ChatColor.translateAlternateColorCodes('&', newprefix);


		format = event.getFormat();
		format = format.replace("{NAME}", newprefix + p.getDisplayName());
		format = format.replace("{USERNAME}",  newprefix + p.getName());
		format = ChatColor.translateAlternateColorCodes('&', format);

		message = event.getMessage();
		if ( p.hasPermission("kingdom.chat.colors") ) {
			message = ChatColor.translateAlternateColorCodes('&', message);
		}
		format = format.replace("{MESSAGE}", message);

		p.sendMessage(format);

		for ( KingdomUser u : KingdomCraft.getApi().getUsers() ) {
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

	@EventHandler
	public void onKingdomChat(KingdomChatEvent e) {
		String format = e.getFormat();
		Player p = e.getPlayer();
		KingdomUser user = KingdomCraft.getApi().getUser(p);

		if ( user.getKingdom() != null ) {
			KingdomObject kd = user.getKingdom();

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

			KingdomRank rank = user.getRank();
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

		if ( hasvaultchat ) {
			if ( chat.getPlayerPrefix(p) != null ) {
				format = format.replace("{RANK}",  chat.getPlayerPrefix(p)); //old one
			}
			if ( chat.getPlayerPrefix(p) != null ) {
				format = format.replace("{PREFIX}",  chat.getPlayerPrefix(p));
			}
			if ( chat.getPlayerSuffix(p) != null ) {
				format = format.replace("{SUFFIX}", chat.getPlayerSuffix(p));
			}
		}

		String[] placeholders = {"RANK", "PREFIX", "SUFFIX", "KINGDOM", "KINGDOM_PREFIX", "KINGDOM_SUFFIX", "KINGDOM_TABPREFIX", "KINGDOM_TABSUFFIX",
			"KINGDOMRANK", "KINGDOMRANK_PREFIX", "KINGDOMRANK_SUFFIX", "KINGDOMRANK_TABPREFIX", "KINGDOMRANK_TABSUFFIX"};
		for ( String placeholder : placeholders ) {
			if ( format.contains("{" + placeholder + "}") ) format = format.replace("{" + placeholder + "}", "");
		}

		e.setFormat(format);
	}
	
	public Channel getChannel(String name) {
		for ( Channel c : channels ) {
			if ( c.getName().equalsIgnoreCase(name) ) return c;
		}
		return null;
	}

	public List<Channel> getChannels() {
		return new ArrayList<>(channels);
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

	public class Channel {

		private final String name;

		private final String format;
		private final String mprefix;

		private final VisibilityType vtype;
		private final boolean alwayson;
		private final boolean permission;

		public Channel(String name, String format, String mprefix, VisibilityType vtype, boolean alwayson, boolean permission) {
			this.name = name;
			this.format = StringEscapeUtils.unescapeJava(format);
			this.mprefix = mprefix;
			this.vtype = vtype;
			this.alwayson = alwayson;
			this.permission = permission;
		}

		public Channel(String name, String format, String mprefix) {
			this(name, format, mprefix, VisibilityType.PUBLIC, false, false);
		}


		public String getName() {
			return name;
		}

		public String getFormat() {
			return format;
		}

		public String getMessagePrefix() {
			return mprefix;
		}

		public VisibilityType getVisibilityType() {
			return vtype;
		}

		public boolean isAlwayson() {
			return alwayson;
		}

		public boolean isPermission() {
			return permission;
		}

	}

	public enum VisibilityType {
		PUBLIC, KINGDOM;
	}

}
