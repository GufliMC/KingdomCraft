/*
 * This file is part of KingdomCraft.
 *
 * KingdomCraft is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * KingdomCraft is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with KingdomCraft. If not, see <https://www.gnu.org/licenses/>.
 */

package com.gufli.kingdomcraft.bukkit.messages;

import com.gufli.kingdomcraft.bukkit.KingdomCraftBukkitPlugin;
import com.gufli.kingdomcraft.common.KingdomCraftImpl;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class MessageManager {

	public MessageManager(KingdomCraftBukkitPlugin plugin) {
		KingdomCraftImpl kdc = plugin.getKdc();

		InputStream in = plugin.getResource("messages.yml");
		try {
			YamlConfiguration config = YamlConfiguration.loadConfiguration(new InputStreamReader(in, StandardCharsets.UTF_8));

			String prefix = config.getString("prefix");
			kdc.getMessageManager().setPrefix(prefix);
			config.set("prefix", null);

			for ( String key : config.getKeys(false) ) {
				kdc.getMessageManager().addMessage(key, config.getString(key));
			}
		} catch (Exception ex) {
			System.out.println("!!! ERROR !!! \nCouldn't retrieve default language! This can cause wrong message display!\n\n");
			throw ex;
		}

	}

}
