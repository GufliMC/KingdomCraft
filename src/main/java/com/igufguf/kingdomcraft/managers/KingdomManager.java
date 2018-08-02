package com.igufguf.kingdomcraft.managers;

import com.igufguf.kingdomcraft.KingdomCraftApi;
import com.igufguf.kingdomcraft.objects.KingdomObject;
import com.igufguf.kingdomcraft.objects.KingdomRank;
import com.igufguf.kingdomcraft.objects.KingdomUser;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

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
public class KingdomManager {

    public final List<String> kingdomOptions = new ArrayList<>(Arrays.asList("prefix", "suffix", "display"));
    public final List<String> rankOptions = new ArrayList<>(Arrays.asList("prefix", "suffix", "permissions", "default"));

    private final KingdomCraftApi api;
    private final File directory;

    private final List<KingdomObject> kingdoms = Collections.synchronizedList(new ArrayList<>());

    public KingdomManager(KingdomCraftApi api) {
        this.api = api;

        directory = new File(api.getPlugin().getDataFolder(), "/kingdoms/");
        if ( !directory.exists() ) directory.mkdirs();

        // create kingdom if none exists
        if ( directory.listFiles().length == 0 ) {
            createKingdom("Gufland");
        }

        // load kingdom
        for ( File file : directory.listFiles() ) {
            String name = file.getName();
            if ( !name.endsWith(".yml") ) continue;

            name = name.substring(0, name.length() - 4); // remove the .yml
            if ( !name.matches("[a-zA-Z]+") ) continue;

            loadKingdom(name);
        }
    }

    public List<KingdomObject> getKingdoms() {
        return new ArrayList<>(kingdoms);
    }

    public KingdomObject getKingdom(String name) {
        for ( KingdomObject ko : kingdoms ) {
            if ( ko.getName().equalsIgnoreCase(name) ) return ko;
        }
        return null;
    }

    public List<Player> getOnlineMembers(KingdomObject kingdom) {
        List<Player> online = new ArrayList<>();

        for ( KingdomUser user : api.getUserManager().getUsers() ) {
            if ( user.getPlayer() != null && user.getKingdom() != null && user.getKingdom().equals(kingdom.getName()) ) {
                online.add(user.getPlayer());
            }
        }

        return online;
    }

    public List<KingdomUser> getMembers(KingdomObject kingdom) {
        List<KingdomUser> members = new ArrayList<>();

        List<KingdomUser> users = api.getUserManager().getAllUsers();
        for ( KingdomUser user : users) {
            if ( user.getKingdom() != null && user.getKingdom().equals(kingdom.getName()) ) {
                members.add(user);
            }
        }

        return members;
    }

    public void loadKingdom(String name) {
        File file = new File(directory, name + ".yml");
        if ( !file.exists() ) return;

        KingdomObject ko = new KingdomObject(name);

        FileConfiguration data = YamlConfiguration.loadConfiguration(file);

        // load kingdom data
        for ( String key : data.getKeys(false) ) {
            if ( data.getConfigurationSection(key) == null ) {
                ko.setData(key, data.get(key));
                ko.delInLocalList("changes", key);
            }
        }

        if ( !data.contains("ranks") ) {
            api.getPlugin().getLogger().warning("Ranks are not configured for kingdom '" + name + "', kingdom will not be loaded!");
            return;
        }

        // load ranks
        for ( String rank : data.getConfigurationSection("ranks").getKeys(false) ) {
            ConfigurationSection cs = data.getConfigurationSection("ranks." + rank);
            if ( cs == null ) continue;
            KingdomRank kr = new KingdomRank(ko, rank);

            // load rank data
            for ( String key : cs.getKeys(false) ) {
                if ( cs.getConfigurationSection(key) == null ) {
                    kr.setData(key, cs.get(key));
                    kr.delInLocalList("changes", key);
                }
            }

            ko.getRanks().add(kr);
        }

        if ( ko.getRanks().size() == 0 ) {
            api.getPlugin().getLogger().warning("The kingdom '" + name + "' has no ranks, kingdom will nog be loaded!");
            return;
        }

        kingdoms.add(ko);
    }

    public void saveKingdom(String name) {
        KingdomObject ko = getKingdom(name);
        if ( ko == null ) return;

        saveKingdom(ko);
    }

    public void saveKingdom(KingdomObject ko) {
        if ( ko == null ) return;

        File file = new File(directory, ko.getName() + ".yml");
        if ( !file.exists() ) return;

        FileConfiguration data = YamlConfiguration.loadConfiguration(file);

        // save kingdom data
        for ( String key : ko.getDataMap().keySet() ) {
            if ( !data.contains(key) ) {
                data.set(key, ko.getData(key));
            } else if ( ko.hasInLocalList("changes", key) ) {
                data.set(key, ko.getData(key));
            }
        }

        // save ranks
        for ( KingdomRank kr : ko.getRanks() ) {
            String path = "ranks." + kr.getName();

            //save rank data
            for ( String key : kr.getDataMap().keySet() ) {
                if ( !data.contains(path + "." + key) ) {
                    data.set(path + "." + key, kr.getData(key));
                } else if ( kr.hasInLocalList("changes", key) ) {
                    data.set(path + "." + key, kr.getData(key));
                }
            }
        }

        try {
            data.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public KingdomObject createKingdom(String name) {
        KingdomObject ko = new KingdomObject(name);

        ChatColor color = ChatColor.values()[new Random().nextInt(ChatColor.values().length)];

        ko.setData("prefix", "&7[&" + color.getChar() + name + "&7]");
        ko.setData("display", "&" + color.getChar() + name);

        KingdomRank member = new KingdomRank(ko, "member");
        member.setData("default", true);
        member.setData("prefix", "&7[&8Member&7]");
        member.setData("permissions", Arrays.asList("kingdom.spawn", "kingdom.channel"));
        ko.getRanks().add(member);

        KingdomRank king = new KingdomRank(ko, "king");
        king.setData("prefix", "&7[&6King&7]");
        king.setData("permissions", Arrays.asList("member", "kingdom.kick", "kingdom.invite"));
        ko.getRanks().add(king);

        kingdoms.add(ko);

        File file = new File(directory, name + ".yml");
        if ( file.exists() ) file.delete();
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        saveKingdom(name);

        return ko;
    }

    public boolean deleteKingdom(String name) {
        KingdomObject ko = getKingdom(name);
        if ( ko == null ) return false;

        return deleteKingdom(ko);
    }

    public boolean deleteKingdom(KingdomObject ko) {
        if ( ko == null ) return false;

        kingdoms.remove(ko);

        File file = new File(directory, ko.getName() + ".yml");
        if ( !file.exists() ) return false;

        file.delete();
        return true;
    }

}
