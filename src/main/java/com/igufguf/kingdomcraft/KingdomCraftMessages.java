package com.igufguf.kingdomcraft;

import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.nio.charset.StandardCharsets;
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
public class KingdomCraftMessages {

	private final KingdomCraft plugin;
	private FileConfiguration messages;

	public KingdomCraftMessages(KingdomCraft plugin) {
		this.plugin = plugin;
		this.reload();
	}

	public void reload() {
		InputStream in = plugin.getResource("messages.yml");

		try {
			messages = YamlConfiguration.loadConfiguration(new InputStreamReader(in, StandardCharsets.UTF_8));
		} catch (Exception ex) {
			System.out.println("!!! ERROR !!! \nCouldn't retrieve default language! This can cause wrong message display!\n\n");
			throw ex;
		}
	}

	public String getMessage(String name) {
		if ( messages.get(name) == null ) return null;
		return ChatColor.translateAlternateColorCodes('&', StringEscapeUtils.unescapeJava(messages.getString(name)));
	}

	public String getMessage(String name, String... placeholders) {
		String message = getMessage(name);
		if ( message == null ) return null;

		for ( int i = 0; i < placeholders.length; i++ ) {
			message = message.replace("{" + i + "}", placeholders[i]);
		}

		return message;
	}

	public boolean isEmpty(String name) {
		return messages.get(name) == null || messages.getString(name).replaceAll(Pattern.quote(" "), "").equals("");
	}

	public void send(CommandSender p, String name, String... placeholders) {
		if ( isEmpty(name) ) return;

		p.sendMessage(plugin.getPrefix() + getMessage(name, placeholders));
	}
}
