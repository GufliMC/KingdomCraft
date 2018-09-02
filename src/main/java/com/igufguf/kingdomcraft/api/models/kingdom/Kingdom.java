package com.igufguf.kingdomcraft.api.models.kingdom;

import com.igufguf.kingdomcraft.api.events.KingdomLoadEvent;
import com.igufguf.kingdomcraft.api.events.KingdomPreLoadEvent;
import com.igufguf.kingdomcraft.api.events.KingdomSaveEvent;
import com.igufguf.kingdomcraft.api.models.storage.Storable;
import com.igufguf.kingdomcraft.api.models.storage.MemoryHolder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

import static com.igufguf.kingdomcraft.utils.KingdomUtils.formatString;
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
public class Kingdom extends MemoryHolder {

	private final Storable kingdomData = new Storable();

	// configuration

	private final String name;
	private final List<KingdomRank> ranks = Collections.synchronizedList(new ArrayList<>());

	private String display;
	private String prefix;
	private String suffix;
	private int maxMembers = Integer.MAX_VALUE;

	// data

	private Location spawn;
	private Map<String, Object> flags = new HashMap<>();

	public Kingdom(String name) {
		this.name = name;
	}

	// getters

	public String getName() {
		return name;
	}

	public String getDisplay() {
		return display != null ? formatString(display) : getName();
	}

	public String getPrefix() {
		return prefix != null ? formatString(prefix) : null;
	}

	public String getSuffix() {
		return suffix != null ? formatString(suffix) : null;
	}

	public Location getSpawn() {
		return spawn;
	}

	public int getMaxMembers() {
		return maxMembers;
	}

	public Storable getKingdomData() {
		return kingdomData;
	}

	public Map<String, Object> getFlags() {
		return flags;
	}

	// setters

	public void setSpawn(Location loc) {
		this.spawn = loc;
	}

	public void setFlags(Map<String, Object> flags) {
		this.flags = flags;
	}

	/** RANKS **/
	
	public boolean hasRank(KingdomRank rank) {
		return this.ranks.contains(rank);
	}

	public List<KingdomRank> getRanks() {
		return this.ranks;
	}

	public KingdomRank getDefaultRank() {
		for ( KingdomRank kr : this.ranks ) {
			if ( kr.isDefault() ) return kr;
		}
		return this.ranks.size() != 0 ? this.ranks.get(0) : null;
	}

	public KingdomRank getRank(String name) {
		for ( KingdomRank kr : getRanks() ) {
			if ( kr.getName().equalsIgnoreCase(name) ) return kr;
		}
		return null;
	}

	private void addRank(KingdomRank rank) {
		if ( this.ranks.contains(rank) ) return;
		this.ranks.add(rank);
	}

	// save

	public void writeData(ConfigurationSection data) {

		//set data
		kingdomData.setData("flags", this.flags);
		kingdomData.setData("spawn", this.spawn);

		kingdomData.save(data);

		for ( KingdomRank kr : getRanks() ) {
			if ( !data.contains("ranks." + kr.getName())) data.createSection("ranks." + kr.getName());
			kr.saveData(data.getConfigurationSection("ranks." + kr.getName()));
		}

		Bukkit.getServer().getPluginManager().callEvent(new KingdomSaveEvent(this, data));
	}

	// load

	// data (can be changed at runtime)
	public void loadData(ConfigurationSection data) {

		// load into kingdomData
		kingdomData.load(data);

		if ( kingdomData.hasData("spawn") ) {
			this.spawn = kingdomData.getData("spawn", Location.class);
		}

		if ( kingdomData.hasData("flags") ) {
			this.flags = mapFromConfiguration(kingdomData.getData("flags"));
		}

		for ( KingdomRank kr : getRanks() ) {
			if ( !data.contains("ranks." + kr.getName())) continue;
			kr.loadData(data.getConfigurationSection("ranks." + kr.getName()));
		}

		Bukkit.getServer().getPluginManager().callEvent(new KingdomLoadEvent(this));
	}

	// configuration (can't be changed at runtime)
	public static Kingdom load(ConfigurationSection data, String name) {
		Kingdom ko = new Kingdom(name);

		ko.display = data.getString("display");
		ko.prefix = data.getString("prefix");
		ko.suffix = data.getString("suffix");

		if ( data.contains("max-members") )
			ko.maxMembers = data.getInt("max-members");

		if ( data.contains("ranks") ) {
			for (String rank : data.getConfigurationSection("ranks").getKeys(false)) {
				KingdomRank kr = KingdomRank.load(data.getConfigurationSection("ranks." + rank), ko, rank);
				ko.addRank(kr);
			}
		}

		Bukkit.getServer().getPluginManager().callEvent(new KingdomPreLoadEvent(ko, data));

		return ko;
	}

}
