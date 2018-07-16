package com.igufguf.kingdomcraft.objects;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.UUID;

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
@SuppressWarnings("deprecation")
public class KingdomUser extends KingdomData {

	private final String name;
	private final String uuid;
	
	public KingdomUser(String name, String uuid) {
		this.name = name;
		this.uuid = uuid;
	}

	public String getName() {
		return name;
	}
	
	public String getUuid() {
		return uuid;
	}
	
	public String getKingdom() {
		return hasData("kingdom") ? getString("kingdom") : null;
	}

	public String getRank() {
		return hasData("rank") ? getString("rank") : null;
	}

	public Player getPlayer() {
		Player p = Bukkit.getPlayer(UUID.fromString(uuid));
		if ( p == null ) Bukkit.getPlayerExact(name);
		return p;
	}

	public OfflinePlayer getOfflinePlayer() {
		return Bukkit.getOfflinePlayer(UUID.fromString(uuid));
	}

	public void save(FileConfiguration file) {
		file.set(uuid + ".name", name);

		for ( String key : this.data.keySet() ) {
			file.set(uuid + "." + key, getData(key));
		}
	}
}
