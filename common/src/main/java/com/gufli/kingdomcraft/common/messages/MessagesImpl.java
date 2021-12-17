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

package com.gufli.kingdomcraft.common.messages;

import com.gufli.kingdomcraft.api.entity.PlatformSender;
import com.gufli.kingdomcraft.api.language.Messages;
import com.gufli.kingdomcraft.common.KingdomCraftPlugin;
import com.gufli.kingdomcraft.common.config.Configuration;

import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.text.StringEscapeUtils.unescapeJava;


public class MessagesImpl implements Messages {

	private final KingdomCraftPlugin plugin;

	private String prefix = "&6KingdomCraft &2> &a";

	private final Map<String, String> messages = new HashMap<>();

	public MessagesImpl(KingdomCraftPlugin plugin) {
		this.plugin = plugin;
	}

	public void setMessages(Configuration... configs) {
		for ( Configuration config : configs ) {
			for ( String key : config.getKeys(false) ) {
				messages.put(key, config.getString(key));
			}
		}
	}

	@Override
	public final String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	@Override
	public String getMessage(String name) {
		if ( name == null ) {
			return null;
		}
		String msg = messages.get(name);
		if ( msg == null ) {
			return null;
		}
		return colorify(unescapeJava(msg));
	}

	@Override
	public String getMessage(String name, boolean colorify, String... placeholders) {
		if ( colorify ) {
			String msg = getMessage(name, placeholders);
			if ( msg == null ) return null;
			return colorify(msg);
		}
		return getMessage(name, placeholders);
	}

	@Override
	public String getMessage(String name, String... placeholders) {
		String message = getMessage(name);
		if ( message == null ) return null;

		for ( int i = 0; i < placeholders.length; i++ ) {
			message = message.replace("{" + i + "}",
					placeholders[i] == null ? "" : placeholders[i]);
		}

		return message;
	}

	private boolean isEmpty(String name) {
		return messages.containsKey(name) && messages.get(name).trim().equals("");
	}

	@Override
	public void send(PlatformSender sender, String name, String... placeholders) {
		if ( isEmpty(name) ) return;
		sender.sendMessage(colorify(prefix) + getMessage(name, placeholders));
	}

	@Override
	public void send(PlatformSender sender, String name, boolean colorify, String... placeholders) {
		if ( isEmpty(name) ) return;
		sender.sendMessage(colorify(prefix) + getMessage(name, colorify, placeholders));
	}

	@Override
	public String colorify(String msg) {
		return plugin.colorify(msg);
	}

	@Override
	public String decolorify(String msg) {
		return plugin.decolorify(msg);
	}

}
