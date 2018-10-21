package com.igufguf.kingdomcraft.api.models.kingdom;

import com.igufguf.kingdomcraft.api.events.KingdomLoadEvent;
import com.igufguf.kingdomcraft.api.events.KingdomUserLoadEvent;
import com.igufguf.kingdomcraft.api.events.KingdomUserSaveEvent;
import com.igufguf.kingdomcraft.api.models.storage.MemoryHolder;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.util.*;

import static com.igufguf.kingdomcraft.utils.KingdomUtils.mapFromConfiguration;

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

public class KingdomUser extends MemoryHolder {

	private final String name;
	private final String uuid;

	private String kingdom;
	private String kingdomRank;

	private Map<String, Boolean> chatChannels = new HashMap<>();
	private List<String> kingdomInvites = new ArrayList<>();

	private boolean socialspy;

	// memory variables

	private final Player player;
	private PermissionAttachment permissions;

	private KingdomUser(String uuid, String name) {
		this.uuid = uuid;
		this.name = name;
		this.player = null;
	}

	private KingdomUser(Player player) {
		this.uuid = player.getUniqueId().toString();
		this.name = player.getName();
		this.player = player;
	}

	// getters

	public String getName() {
		return name;
	}
	
	public String getUuid() {
		return uuid;
	}
	
	public String getKingdom() {
		return kingdom;
	}

	public String getRank() {
		return kingdomRank;
	}

	public boolean isChannelEnabled(String channel) {
		return chatChannels.containsKey(channel) && chatChannels.get(channel);
	}

	public boolean hasKingdomInvite(String kingdom) {
		return this.kingdomInvites.contains(kingdom);
	}

	public PermissionAttachment getPermissions() {
		return permissions;
	}

	public boolean isSocialSpyEnabled() {
		return socialspy;
	}

	// setters

	public void setRank(String kingdomRank) {
		this.kingdomRank = kingdomRank;
	}

	public void setKingdom(String kingdom) {
		this.kingdom = kingdom;
	}

	public void setChannelEnabled(String channel, boolean enabled) {
		chatChannels.put(channel, enabled);
	}

	public void resetChannels() {
		chatChannels.clear();
	}

	public void addKingdomInvite(String kingdom) {
		if ( this.kingdomInvites.contains(kingdom) ) return;
		this.kingdomInvites.add(kingdom);
	}

	public void delKingdomInvite(String kingdom) {
		while ( this.kingdomInvites.contains(kingdom) ) this.kingdomInvites.remove(kingdom);
	}

	public void setPermissions(PermissionAttachment permissions) {
		this.permissions = permissions;
	}

	public void setSocialSpyEnabled(boolean enabled) {
		this.socialspy = enabled;
	}

	// get players

	public Player getPlayer() {
		return player;
	}

	public OfflinePlayer getOfflinePlayer() {
		if ( player != null ) return player;
		return Bukkit.getOfflinePlayer(UUID.fromString(uuid));
	}

	// save

	public void save(ConfigurationSection data) {
		data.set("name", name);
		data.set("kingdom", kingdom);
		data.set("rank", kingdomRank);
		data.set("channels", chatChannels);
		data.set("invites", kingdomInvites);
		data.set("socialspy", socialspy);

		Bukkit.getServer().getPluginManager().callEvent(new KingdomUserSaveEvent(this, data));
	}

	// load

	public static KingdomUser load(ConfigurationSection data, Player player) {
		KingdomUser user = new KingdomUser(player);
		return load(user, data);
	}

	public static KingdomUser load(ConfigurationSection data, String uuid) {
		KingdomUser user = new KingdomUser(uuid, data.getString("name"));
		return load(user, data);
	}

	private static KingdomUser load(KingdomUser user, ConfigurationSection data) {
		if ( data != null ) {
			user.setKingdom(data.getString("kingdom"));
			user.setRank(data.getString("rank"));
			user.setSocialSpyEnabled(data.getBoolean("socialspy"));

			if (data.contains("channels")) {
				Map<String, Object> chatChannels = mapFromConfiguration(data.get("channels"));
				for (String channel : chatChannels.keySet())
					user.setChannelEnabled(channel, (boolean) chatChannels.get(channel));
			}

			if (data.contains("invites")) {
				user.kingdomInvites = data.getStringList("invites");
			}
		}

		Bukkit.getServer().getPluginManager().callEvent(new KingdomUserLoadEvent(user, data));
		return user;
	}

}
