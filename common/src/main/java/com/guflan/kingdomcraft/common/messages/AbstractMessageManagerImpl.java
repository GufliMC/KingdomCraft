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

package com.guflan.kingdomcraft.common.messages;

import com.guflan.kingdomcraft.api.entity.PlatformPlayer;
import com.guflan.kingdomcraft.api.entity.PlatformSender;
import com.guflan.kingdomcraft.api.messages.MessageManager;

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
public abstract class AbstractMessageManagerImpl implements MessageManager {

	private String prefix;
	private final Map<String, String> messages = new HashMap<>();

	@Override
	public void registerMessage(String name, String msg) {
		messages.put(name, msg);
	}

	@Override
	public void unregisterMessage(String name) {
		messages.remove(name);
	}

	@Override
	public String getPrefix() {
		return prefix;
	}

	@Override
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	private boolean isEmpty(String name) {
		return !messages.containsKey(name) || messages.get(name).replaceAll(Pattern.quote(" "), "").equals("");
	}

	@Override
	public String getMessage(String name) {
		if ( !messages.containsKey(name) ) return null;
		return colorify(messages.get(name));
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

}
