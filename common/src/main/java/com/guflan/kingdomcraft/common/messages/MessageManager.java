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

package com.guflan.kingdomcraft.common.messages;

import com.guflan.kingdomcraft.api.entity.PlatformPlayer;
import com.guflan.kingdomcraft.api.entity.PlatformSender;
import com.guflan.kingdomcraft.common.KingdomCraftImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class MessageManager {

	private final KingdomCraftImpl kdc;

	private String prefix;
	private final Map<String, String> messages = new HashMap<>();

	public MessageManager(KingdomCraftImpl kdc) {
		this.kdc = kdc;
	}

	public final void addMessage(String name, String msg) {
		messages.put(name, msg);
	}

	public final void removeMessage(String name) {
		messages.remove(name);
	}

	public final String getPrefix() {
		return prefix;
	}

	public final void setPrefix(String prefix) {
		this.prefix = colorify(prefix);
	}

	private boolean isEmpty(String name) {
		return !messages.containsKey(name) || messages.get(name).replaceAll(Pattern.quote(" "), "").equals("");
	}

	public boolean hasMessage(String name) {
		return messages.containsKey(name);
	}

	public String getMessage(String name) {
		if ( !hasMessage(name) ) return null;
		return colorify(messages.get(name));
	}

	public String getMessage(String name, boolean colorify, String... placeholders) {
		if ( colorify ) {
			String msg = getMessage(name, placeholders);
			if ( msg == null ) return null;
			return colorify(msg);
		}
		return getMessage(name, placeholders);
	}

	public String getMessage(String name, String... placeholders) {
		String message = getMessage(name);
		if ( message == null ) return null;

		for ( int i = 0; i < placeholders.length; i++ ) {
			message = message.replace("{" + i + "}",
					placeholders[i] == null ? "" : placeholders[i]);
		}

		return message;
	}

	public void send(PlatformPlayer player, String name, String... placeholders) {
		if ( isEmpty(name) ) return;
		player.sendMessage(colorify(prefix) + getMessage(name, placeholders));
	}

	public void send(PlatformSender sender, String name, String... placeholders) {
		if ( isEmpty(name) ) return;
		sender.sendMessage(colorify(prefix) + getMessage(name, placeholders));
	}

	public void send(PlatformPlayer player, String name, boolean colorify, String... placeholders) {
		if ( isEmpty(name) ) return;
		player.sendMessage(colorify(prefix) + getMessage(name, colorify, placeholders));
	}

	public void send(PlatformSender sender, String name, boolean colorify, String... placeholders) {
		if ( isEmpty(name) ) return;
		sender.sendMessage(colorify(prefix) + getMessage(name, colorify, placeholders));
	}

	public String colorify(String msg) {
		return kdc.getPlugin().colorify(msg);
	}

	public String decolorify(String msg) {
		return kdc.getPlugin().decolorify(msg);
	}

}
