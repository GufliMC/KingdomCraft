package com.igufguf.kingdomcraft.managers;

import com.igufguf.kingdomcraft.KingdomCraftApi;
import com.igufguf.kingdomcraft.events.KingdomJoinEvent;
import com.igufguf.kingdomcraft.events.KingdomLeaveEvent;
import com.igufguf.kingdomcraft.objects.KingdomObject;
import com.igufguf.kingdomcraft.objects.KingdomRank;
import com.igufguf.kingdomcraft.objects.KingdomUser;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
public class UserManager {

    private final KingdomCraftApi api;

    private final List<KingdomUser> users = Collections.synchronizedList(new ArrayList<KingdomUser>());

    private final File file;
    private final FileConfiguration data;

    public UserManager(KingdomCraftApi api) throws IOException {
        this.api = api;

        file = new File(api.getPlugin().getDataFolder() + "/data", "users.yml");
        if ( !file.exists() ) {
            file.createNewFile();
        }

        data = YamlConfiguration.loadConfiguration(file);
    }

    public List<KingdomUser> getUsers() {
        return new ArrayList<>(users);
    }

    public void registerUser(KingdomUser user) {
        if ( !users.contains(user) ) users.add(user);

        System.out.println(user.getName() + " registered (uuid = " + user.getUuid() + ")");
    }

    public void unregisterUser(KingdomUser user) {
        users.remove(user);
    }

    public KingdomUser getUser(Player p) {
        if ( p == null || !p.isOnline() ) return (p != null ? getOfflineUser(p.getUniqueId()) : null);
        for ( KingdomUser ku : users ) {
            if ( ku.getUuid().equals(p.getUniqueId().toString()) ) return ku;
        }
        return null;
    }

    public KingdomUser getUser(String username) {
        Player p = Bukkit.getPlayerExact(username);
        if ( p != null ) return getUser(p);

        return getOfflineUser(Bukkit.getOfflinePlayer(username).getUniqueId());
    }

    public KingdomUser getOfflineUser(UUID uuid) {
        if ( data.get(uuid.toString()) == null ) {
            return new KingdomUser(Bukkit.getPlayer(uuid) != null ? Bukkit.getPlayer(uuid).getName() :
                    Bukkit.getOfflinePlayer(uuid).getName(), uuid.toString());
        }

        String name = data.getString(uuid.toString() + ".name");

        KingdomUser ku = new KingdomUser(name, uuid.toString());

        for ( String key : data.getConfigurationSection(uuid.toString()).getKeys(false) ) {
            if ( key.equalsIgnoreCase("name") ) continue;
            ku.setData(key, data.get(uuid.toString() + "." + key));
            ku.delInLocalList("changes", key);
        }

        // failsaves on removed kingdoms / ranks while user was offline
        if ( ku.getKingdom() != null && getKingdom(ku) == null ) {
            ku.setData("kingdom", null);
        }

        if ( ku.getRank() != null && getRank(ku) == null ) {
            ku.setData("rank", null);
        }

        // set user in default rank if no rank was set and he is in a kingdom
        if ( getKingdom(ku) != null && ku.getRank() == null ) {
            setDefaultRank(ku);
        }

        return ku;
    }

    public List<KingdomUser> getAllUsers() {
        List<KingdomUser> users = new ArrayList<>();

        for ( String uuid : data.getKeys(false) ) {
            KingdomUser user = new KingdomUser(data.getString(uuid + ".name"), uuid);

            for ( String key : data.getConfigurationSection(uuid).getKeys(false) ) {
                if ( key.equalsIgnoreCase("name") ) continue;
                user.setData(key, data.get(uuid + "." + key));
            }

            users.add(user);
        }

        return users;
    }

    public KingdomObject getKingdom(KingdomUser user) {
        return api.getKingdomManager().getKingdom(user.getKingdom());
    }

    public KingdomRank getRank(KingdomUser user) {
        KingdomObject ko = api.getKingdomManager().getKingdom(user.getKingdom());
        if ( ko == null ) return null;

        return ko.getRank(user.getRank());
    }

    public void setKingdom(KingdomUser user, KingdomObject kingdom) {
        if ( kingdom == null ) {
            KingdomObject oldKingdom = getKingdom(user);
            KingdomRank oldRank = getRank(user);

            user.setData("kingdom", null);
            user.setData("rank", null);
            user.setData("channels", null);

            Bukkit.getPluginManager().callEvent(new KingdomLeaveEvent(user, oldKingdom, oldRank));

            save(user);
            return;
        }

        user.setData("kingdom", kingdom.getName());
        setDefaultRank(user);

        if ( user.hasData("invites") ) {
            List<String> invites = (List<String>) user.getData("invites");
            invites.remove(kingdom.getName());
        }

        api.getPermissionManager().refreshPermissions(user);

        Bukkit.getPluginManager().callEvent(new KingdomJoinEvent(user));

        save(user);
    }

    public void setRank(KingdomUser user, KingdomRank rank) {
        KingdomObject ko = getKingdom(user);
        if ( ko == null || !ko.hasRank(rank) ) return;

        user.setData("rank", rank.getName());
        api.getPermissionManager().refreshPermissions(user);
    }

    public void setDefaultRank(KingdomUser user) {
        KingdomObject kingdom = getKingdom(user);
        if ( kingdom == null  ) return;

        if ( kingdom.getDefaultRank() != null ) {
            user.setData("rank", kingdom.getDefaultRank().getName());
        } else {
            user.setData("rank", kingdom.getRanks().get(0).getName());
        }
    }

    public void save(KingdomUser user) {
        data.set(user.getUuid() + ".name", user.getName());

        for ( String key : user.getDataMap().keySet() ) {
            data.set(user.getUuid() + "." + key, user.getData(key));
        }

        try {
            data.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
