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

import com.guflan.kingdomcraft.common.messages.AbstractMessageManager;
import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class MessageManagerImpl extends AbstractMessageManager {

	public MessageManagerImpl(Plugin kingdomCraft) {
		InputStream in = kingdomCraft.getResource("messages.yml");
		try {
			YamlConfiguration config = YamlConfiguration.loadConfiguration(new InputStreamReader(in, StandardCharsets.UTF_8));

			String prefix = config.getString("prefix");
			setPrefix(prefix);
			config.set("prefix", null);

			for ( String key : config.getKeys(false) ) {
				addMessage(key, config.getString(key));
			}
		} catch (Exception ex) {
			System.out.println("!!! ERROR !!! \nCouldn't retrieve default language! This can cause wrong message display!\n\n");
			throw ex;
		}
	}


	public String getMessage(String name) {
		String msg = super.getMessage(name);
		return msg == null ? null : StringEscapeUtils.unescapeJava(msg);
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
