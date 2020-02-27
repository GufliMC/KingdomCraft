package com.igufguf.kingdomcraft.handlers;

import com.igufguf.kingdomcraft.KingdomCraft;
import com.igufguf.kingdomcraft.api.handlers.KingdomHandler;
import com.igufguf.kingdomcraft.api.models.database.Configurable;
import com.igufguf.kingdomcraft.api.models.database.StorageManager;
import com.igufguf.kingdomcraft.api.models.kingdom.Kingdom;
import com.igufguf.kingdomcraft.api.models.kingdom.KingdomRank;
import com.igufguf.kingdomcraft.api.models.kingdom.KingdomUser;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
public class SimpleKingdomHandler extends Configurable implements KingdomHandler {

    private final KingdomCraft plugin;

    private final StorageManager storageManager;

    private final List<KingdomRank> defaultRanks = new ArrayList<>();
    private final List<Kingdom> kingdoms = Collections.synchronizedList(new ArrayList<>());

    public SimpleKingdomHandler(KingdomCraft plugin) {
        super(new File(plugin.getDataFolder(), "kingdoms.yml"));

        this.plugin = plugin;
        this.storageManager = new StorageManager(new File(plugin.getDataFolder() + "/data", "kingdoms.data"));

        // save default kingdoms file
        if ( !getConfigFile().exists() ) {
            plugin.saveResource("kingdoms.yml", true);
            super.loadConfig();
        }

        FileConfiguration config = getConfiguration();

        // load default ranks
        if ( config.contains("ranks") ) {
            for ( String rank : config.getConfigurationSection("ranks").getKeys(false) ) {
                KingdomRank kr = KingdomRank.load(config.getConfigurationSection("ranks." + rank), null, rank);
                defaultRanks.add(kr);
            }
        }

        // load kingdoms
        if ( config.contains("kingdoms") ) {
            for (String kingdom : config.getConfigurationSection("kingdoms").getKeys(false)) {
                load(kingdom);
            }
        }

        // oh oh
        if ( kingdoms.size() == 0 ) {
            plugin.getLogger().info("\t ");
            plugin.getLogger().info("!!! WARNING !!!");
            plugin.getLogger().info("\t ");
            plugin.getLogger().info("Couldn't load any kingdoms, is your kingdoms.yml file correct?");
            plugin.getLogger().info("\t ");
            plugin.getLogger().info("If this is unexpected, shut down your server immediately to prevent data loss and fix your configuration.");
            plugin.getLogger().info("Unmodified data from the previous run is saved in a .prev extension. Take a backup of these files!");
            plugin.getLogger().info("\t ");
        } else {
            StringBuilder sb = new StringBuilder();
            for ( Kingdom kd : kingdoms ) {
                sb.append(", ").append(kd.getName());
            }

            plugin.getLogger().info("Loaded " + kingdoms.size() + " kingdoms: " + sb.toString().substring(2));
        }

    }

    // kingdom getters

    @Override
    public List<Kingdom> getKingdoms() {
        return kingdoms;
    }

    @Override
    public Kingdom getKingdom(String name) {
        for ( Kingdom ko : kingdoms ) {
            if ( ko.getName().equalsIgnoreCase(name) ) return ko;
        }
        return null;
    }

    // member getters

    @Override
    public List<Player> getOnlineMembers(Kingdom kingdom) {
        List<Player> online = new ArrayList<>();

        for ( KingdomUser user : plugin.getApi().getUserHandler().getUsers() ) {
            if ( user.getPlayer() != null && user.getKingdom() != null && user.getKingdom().equals(kingdom.getName()) ) {
                online.add(user.getPlayer());
            }
        }

        return online;
    }

    @Override
    public List<KingdomUser> getMembers(Kingdom kingdom) {
        List<KingdomUser> members = new ArrayList<>();

        List<KingdomUser> users = plugin.getApi().getUserHandler().getAllUsers();
        for ( KingdomUser user : users) {
            if ( user.getKingdom() != null && user.getKingdom().equals(kingdom.getName()) ) {
                members.add(user);
            }
        }

        return members;
    }

    // mangement

    @Override
    public Kingdom load(String name) {
        if ( !getConfiguration().contains("kingdoms." + name) ) return null;

        ConfigurationSection data = getConfiguration().getConfigurationSection("kingdoms." + name);
        Kingdom ko = Kingdom.load(data, name, defaultRanks);

        if ( ko.getRanks().size() == 0 ) {
            plugin.getLogger().warning("The kingdom '" + name + "' has no ranks, kingdom will not be loaded!");
            return null;
        }

        // load kingdom data
        if ( this.storageManager.getStorageData().contains(name) ) {
            ko.loadData(this.storageManager.getStorageData().getConfigurationSection(name));
        }

        kingdoms.add(ko);
        return ko;
    }

    @Override
    public void save(Kingdom ko) {
        if ( ko == null ) return;

        if ( !this.storageManager.getStorageData().contains(ko.getName()) ) {
            this.storageManager.getStorageData().createSection(ko.getName());
        }

        ko.writeData(this.storageManager.getStorageData().getConfigurationSection(ko.getName()));

        this.storageManager.save();
    }

    @Override
    public Object getKingdomConfigOption(Kingdom kingdom, String option) {
        if ( kingdom == null ) return null;
        return getConfiguration().get("kingdoms." + kingdom.getName() + "." + option);
    }

    @Override
    public Object getRankConfigOption(KingdomRank rank, String option) {
        if ( rank == null ) return null;

        if ( rank.getKingdom() != null ) {
            ConfigurationSection rankdata = getConfiguration().getConfigurationSection("kingdoms." + rank.getKingdom().getName() + ".ranks." + rank.getName());
            if (rankdata != null) {
                return rankdata.get(option);
            }
        }

        // kingdom doesn't override rank or rank has no kingdom
        return getConfiguration().get("ranks." + rank.getName() + "." + option);
    }

    @Override
    public boolean containsKingdomConfigOption(Kingdom kingdom, String option) {
        return getKingdomConfigOption(kingdom, option) != null;
    }

    @Override
    public boolean containsRankConfigOption(KingdomRank rank, String option) {
        return getRankConfigOption(rank, option) != null;
    }

}
