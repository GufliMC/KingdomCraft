package com.igufguf.kingdomcraft.objects;

import org.apache.commons.lang3.StringEscapeUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Copyrighted 2017 iGufGuf
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
public class KingdomObject extends KingdomData {

	private final List<KingdomRank> ranks = Collections.synchronizedList(new ArrayList<KingdomRank>());

	private final String name;

	public KingdomObject(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String getDisplay() {
		return hasData("display") ? ChatColor.translateAlternateColorCodes('&', StringEscapeUtils.unescapeJava((String) getData("display"))) : getName();
	}

	public KingdomRank getDefaultRank() {
		for ( KingdomRank kr : this.ranks ) {
			if ( kr.hasData("default") && kr.getData("default") instanceof Boolean && (boolean) kr.getData("default") ) return kr;
		}
		return this.ranks.size() != 0 ? this.ranks.get(0) : null;
	}

	public Location getSpawn() {
		if ( !hasData("spawn") ) return null;
		String[] split = ((String) getData("spawn")).split(" , ");
		
		if ( split.length < 4 && Bukkit.getWorld(split[0]) == null ) return null;
		
		Location loc = new Location(Bukkit.getWorld(split[0]), Double.valueOf(split[1]), Double.valueOf(split[2]), Double.valueOf(split[3]));
		if ( split.length == 6 ) {
			loc.setYaw(Float.valueOf(split[4]));
			loc.setYaw(Float.valueOf(split[5]));
		}
		
		return loc;
	}
	
	public void setSpawn(Location loc) {
		setData("spawn", loc.getWorld().getName() + " , " + loc.getX() + " , " + loc.getY() + " , " + loc.getZ() + " , " + loc.getYaw() + " , " + loc.getPitch());
	}
	
	public boolean hasRank(KingdomRank rank) {
		return this.ranks.contains(rank);
	}

	public List<KingdomRank> getRanks() {
		return this.ranks;
	}

	public KingdomRank getRank(String name) {
		for ( KingdomRank kr : getRanks() ) {
			if ( kr.getName().equalsIgnoreCase(name) ) return kr;
		}
		return null;
	}

}
