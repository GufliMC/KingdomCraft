package com.igufguf.kingdomcraft.objects;

import com.igufguf.kingdomcraft.KingdomCraft;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

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

	public KingdomObject(String name, FileConfiguration file) {
		this.name = name;

		for ( String key : file.getKeys(false) ) {
			if ( file.getConfigurationSection(key) == null ) {
				setData(key, file.get(key));
				delInLocalList("changes", key);
			} else if ( key.equalsIgnoreCase("ranks") ) {
				for ( String rank : file.getConfigurationSection("ranks").getKeys(false) ) {
					ConfigurationSection cs = file.getConfigurationSection("ranks." + rank);
					if ( cs != null ) {
						KingdomRank kr = new KingdomRank(rank, cs);
						this.ranks.add(kr);
					}
				}
			}
		}
	}

	public String getName() {
		return name;
	}

	public KingdomRank getDefaultRank() {
		for ( KingdomRank kr : this.ranks ) {
			if ( kr.hasData("default") && kr.getData("default") instanceof Boolean && (boolean) kr.getData("default") ) return kr;
		}
		return this.ranks.size() != 0 ? this.ranks.get(0) : null;
	}

	public ArrayList<Player> getOnlineMembers() {
		ArrayList<Player> online = new ArrayList<>();
		
		for ( KingdomUser user : KingdomCraft.getApi().getUsers() ) {
			if ( user.getKingdom() == this && user.getPlayer() != null ) {
				online.add(user.getPlayer());
			}
		}
		
		return online;
	}

	public ArrayList<KingdomUser> getMembers() {
		ArrayList<KingdomUser> members = new ArrayList<>();

		List<KingdomUser> users = KingdomCraft.getApi().getAllUsers();
		for ( KingdomUser user : users) {
			if ( user.getKingdom() == this ) {
				members.add(user);
			}
		}

		return members;
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

	public ArrayList<KingdomRank> getRanks() {
		return new ArrayList<>(this.ranks);
	}

	public KingdomRank getRank(String name) {
		for ( KingdomRank kr : getRanks() ) {
			if ( kr.getName().equalsIgnoreCase(name) ) return kr;
		}
		return null;
	}

	public void save(FileConfiguration file) {
		for ( String key : this.data.keySet() ) {
			if ( file.get(key) == null ) {
				file.set(key, this.data.get(key));
			} else if ( hasInLocalList("changes", key) ) {
				file.set(key, this.data.get(key));
			}
		}

		for ( KingdomRank kr : this.ranks ) {
			kr.save(file, "ranks." + kr.getName());
		}
	}

	// PERMISSIONS

	/*
	public static final HashMap<String, PermissionAttachment> playerperms = new HashMap<String, PermissionAttachment>();
	
	public void givePerms(Player p, String rank) {
		if ( rank == null || p == null) return;
		ArrayList<String> perms;
		
		File file = new File(KingdomCraft.getPlugin(KingdomCraft.class).getDataFolder() + "/kingdoms/", name + ".yml");
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		String path = "ranks." + rank + ".permissions";
		if ( config.get(path) == null ) return;
		
		perms = new ArrayList<String>(config.getStringList(path));
		
		ArrayList<String> applyperms = new ArrayList<String>();
		
		PermissionAttachment attachment = p.addAttachment(KingdomCraft.getPlugin(KingdomCraft.class));
		
		for ( String perm : perms ) {
			if ( hasRank(perm) ) {
				ArrayList<String> extrarankperms = new ArrayList<String>(config.getStringList("ranks." + perm + ".permissions"));
				for ( String extraperm : extrarankperms ) {
					if ( !hasRank(extraperm) ) {
						if ( !applyperms.contains(extraperm) ) applyperms.add(extraperm);
					}
				}
			} else {
				if ( !applyperms.contains(perm) ) applyperms.add(perm);
			}
		}
		
		for ( String perm : applyperms ) {
			try {
				if (perm.startsWith("-")) attachment.setPermission(perm.replaceFirst("-", ""), false);
				else if (!attachment.getPermissions().containsKey(perm)) attachment.setPermission(perm, true);
			} catch (Exception e) {
				System.out.println("Could not give player " + p.getName() + " with rank " + rank + " the permission '" + perm + "'!");
			}
		}
		
		playerperms.put(p.getName(), attachment);
	}
	*/
}
