package com.igufguf.kingdomcraft.bukkit;

import com.igufguf.kingdomcraft.api.managers.MessageManager;
import com.igufguf.kingdomcraft.api.commands.CommandSender;
import com.igufguf.kingdomcraft.api.domain.Player;
import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.InputStream;
import java.io.InputStreamReader;
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
public class BukkitMessageManager implements MessageManager {

	private final String prefix;
	private final FileConfiguration messages;

	public BukkitMessageManager(KingdomCraft kingdomCraft) {
		InputStream in = kingdomCraft.getResource("messages.yml");
		try {
			messages = YamlConfiguration.loadConfiguration(new InputStreamReader(in, StandardCharsets.UTF_8));
		} catch (Exception ex) {
			System.out.println("!!! ERROR !!! \nCouldn't retrieve default language! This can cause wrong message display!\n\n");
			throw ex;
		}

		prefix = messages.getString("prefix");
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

	private boolean isEmpty(String name) {
		return messages.get(name) == null || messages.getString(name).replaceAll(Pattern.quote(" "), "").equals("");
	}

	public void send(Player player, String name, String... placeholders) {
		if ( isEmpty(name) ) return;

		org.bukkit.entity.Player bplayer = Bukkit.getPlayer(player.getUniqueId());
		if ( bplayer != null ) {
			bplayer.sendMessage(prefix + getMessage(name, placeholders));
		}
	}

	@Override
	public void send(CommandSender sender, String name, String... placeholders) {
		if ( isEmpty(name) ) return;
		sender.sendMessage(prefix + getMessage(name, placeholders));
	}
}
