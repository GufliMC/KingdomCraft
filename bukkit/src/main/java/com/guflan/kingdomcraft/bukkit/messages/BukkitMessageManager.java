/*
 * This file is part of KingdomCraft.
 *
 * KingdomCraft is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * KingdomCraft is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with KingdomCraft. If not, see <https://www.gnu.org/licenses/>.
 */

package com.guflan.kingdomcraft.bukkit.messages;

import com.guflan.kingdomcraft.api.entity.PlatformSender;
import com.guflan.kingdomcraft.api.entity.PlatformPlayer;
import com.guflan.kingdomcraft.api.messages.MessageManager;
import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
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
public class BukkitMessageManager implements MessageManager {

	private final String prefix;
	private final Map<String, String> messages = new HashMap<>();

	public BukkitMessageManager(Plugin kingdomCraft) {
		InputStream in = kingdomCraft.getResource("messages.yml");
		try {
			YamlConfiguration config = YamlConfiguration.loadConfiguration(new InputStreamReader(in, StandardCharsets.UTF_8));

			prefix = config.getString("prefix");
			config.set("prefix", null);

			for ( String key : config.getKeys(false) ) {
				messages.put(key, config.getString(key));
			}
		} catch (Exception ex) {
			System.out.println("!!! ERROR !!! \nCouldn't retrieve default language! This can cause wrong message display!\n\n");
			throw ex;
		}
	}

	@Override
	public void registerMessage(String name, String msg) {
		messages.put(name, msg);
	}

	@Override
	public void unregisterMessage(String name) {
		messages.remove(name);
	}

	private boolean isEmpty(String name) {
		return !messages.containsKey(name) || messages.get(name).replaceAll(Pattern.quote(" "), "").equals("");
	}

	@Override
	public String getMessage(String name) {
		if ( !messages.containsKey(name) ) return null;
		return ChatColor.translateAlternateColorCodes('&', StringEscapeUtils.unescapeJava(messages.get(name)));
	}

	@Override
	public String getMessage(String name, String... placeholders) {
		String message = getMessage(name);
		if ( message == null ) return null;

		for ( int i = 0; i < placeholders.length; i++ ) {
			message = message.replace("{" + i + "}", colorify(placeholders[i]));
		}

		return message;
	}

	@Override
	public void send(PlatformPlayer player, String name, String... placeholders) {
		if ( isEmpty(name) ) return;
		player.sendMessage(prefix + getMessage(name, placeholders));
	}

	@Override
	public void send(PlatformSender sender, String name, String... placeholders) {
		if ( isEmpty(name) ) return;
		sender.sendMessage(prefix + getMessage(name, placeholders));
	}

	@Override
	public String colorify(String msg) {
		return ChatColor.translateAlternateColorCodes('&', msg);
	}

	@Override
	public String decolorify(String msg) {
		return ChatColor.stripColor(msg);
	}
}
