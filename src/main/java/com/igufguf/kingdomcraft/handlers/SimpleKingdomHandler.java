package com.igufguf.kingdomcraft.handlers;

import com.igufguf.kingdomcraft.KingdomCraft;
import com.igufguf.kingdomcraft.api.handlers.KingdomHandler;
import com.igufguf.kingdomcraft.api.models.database.Configurable;
import com.igufguf.kingdomcraft.api.models.database.StorageManager;
import com.igufguf.kingdomcraft.api.models.kingdom.Kingdom;
import com.igufguf.kingdomcraft.api.models.kingdom.KingdomUser;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
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
public class SimpleKingdomHandler extends Configurable implements KingdomHandler {

    private final KingdomCraft plugin;

    private final StorageManager storageManager;

    private final List<Kingdom> kingdoms = Collections.synchronizedList(new ArrayList<>());

    public SimpleKingdomHandler(KingdomCraft plugin) {
        super(new File(plugin.getDataFolder(), "kingdoms.yml"));

        this.plugin = plugin;
        this.storageManager = new StorageManager(new File(plugin.getDataFolder() + "/data", "kingdoms.data"));

        FileConfiguration config = getConfiguration();

        if ( config.contains("kingdoms") ) {
            for (String kingdom : config.getConfigurationSection("kingdoms").getKeys(false)) {
                load(kingdom);
            }
        }

        // no kingdoms loaded? there are none! Create an example one.
        if ( getKingdoms().size() == 0 ) {
            createKingdom("Gufland");
        }

    }

    // kingdom getters

    @Override
    public List<Kingdom> getKingdoms() {
        return new ArrayList<>(kingdoms);
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
        Kingdom ko = Kingdom.load(data, name);

        if ( ko.getRanks().size() == 0 ) {
            plugin.getLogger().warning("The kingdom '" + name + "' has no ranks, kingdom will nog be loaded!");
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


    private Kingdom createKingdom(String name) {

        ChatColor color = ChatColor.values()[new Random().nextInt(ChatColor.values().length)];

        FileConfiguration tmp = new YamlConfiguration();
        tmp.set("prefix", "&7[&" + color.getChar() + name + "&7]");
        tmp.set("display", "&" + color.getChar() + name);

        tmp.set("ranks.member.default", true);
        tmp.set("ranks.member.prefix", "&7[&8Member&7]");
        tmp.set("ranks.member.permissions", Arrays.asList("kingdom.spawn", "kingdom.leave"));

        tmp.set("ranks.king.prefix", "&7[&6King&7]");
        tmp.set("ranks.king.permissions", Arrays.asList("member", "kingdom.kick", "kingdom.invite"));

        Kingdom ko = Kingdom.load(tmp, name);
        kingdoms.add(ko);

        getConfiguration().set("kingdoms." + name, tmp);
        saveConfiguration();

        return ko;
    }

    /*
    @Override
    public boolean deleteKingdom(Kingdom ko) {
        if ( ko == null ) return false;

        for ( KingdomUser user : api.getUserHandler().getUsers() ) {
            if ( user.getKingdom().equals(ko.getName()) ) {
                api.getUserHandler().setKingdom(user, null);
            }
        }

        kingdoms.remove(ko);

        File file = new File(directory, ko.getName() + ".yml");
        if ( !file.exists() ) return true;

        file.delete();
        return true;
    }
    */

}
