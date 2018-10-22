package com.igufguf.kingdomcraft.handlers;

import com.igufguf.kingdomcraft.KingdomCraft;
import com.igufguf.kingdomcraft.api.events.KingdomJoinEvent;
import com.igufguf.kingdomcraft.api.events.KingdomLeaveEvent;
import com.igufguf.kingdomcraft.api.handlers.KingdomUserHandler;
import com.igufguf.kingdomcraft.api.models.database.StorageManager;
import com.igufguf.kingdomcraft.api.models.kingdom.Kingdom;
import com.igufguf.kingdomcraft.api.models.kingdom.KingdomRank;
import com.igufguf.kingdomcraft.api.models.kingdom.KingdomUser;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.io.File;
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
public class SimpleUserHandler extends StorageManager implements KingdomUserHandler {

    private final KingdomCraft plugin;

    private final List<KingdomUser> users = Collections.synchronizedList(new ArrayList<>());

    public SimpleUserHandler(KingdomCraft plugin) {
        super(new File(plugin.getDataFolder() + "/data", "users.data"));
        this.plugin = plugin;
    }

    @Override
    public void unloadUser(KingdomUser user) {
        users.remove(user);
        plugin.getPermissionManager().clear(user);
        save(user);
    }

    @Override
    public List<KingdomUser> getUsers() {
        return new ArrayList<>(users);
    }

    @Override
    public KingdomUser getUser(Player p) {
        if ( p == null ) return null;

        for ( KingdomUser ku : users ) {
            if ( ku.getPlayer() == p ) return ku;
        }

        return null;
    }

    @Override
    public KingdomUser getUser(String username) {
        Player p = Bukkit.getPlayerExact(username);
        if ( p != null ) return getUser(p);
        return null;
    }

    private KingdomUser fixUser(KingdomUser user) {
        // failsaves on removed kingdoms / ranks while user was offline
        if ( user.getKingdom() != null && getKingdom(user) == null ) {
            user.setKingdom(null);
        }

        // if rank doesn't exists, empty it
        if ( user.getRank() != null && getRank(user) == null ) {
            user.setRank(null);
        }

        // set user in default rank if no rank was set and he is in a kingdom
        if ( getKingdom(user) != null && user.getRank() == null ) {
            setDefaultRank(user);
        }

        return user;
    }

    @Override
    public KingdomUser getOfflineUser(UUID uuid, String name) {
        KingdomUser user = null;

        // search by uuid
        if ( uuid != null && getStorageData().contains(uuid.toString()) ) {
            user = KingdomUser.load(getStorageData().getConfigurationSection(uuid.toString()), uuid.toString());
        }

        // uuid is null or has no results, search by name
        if ( user == null && name != null) {
            for (String uuidkey : getStorageData().getKeys(false)) {
                if ( !getStorageData().contains(uuidkey + ".name") ) continue;
                if ( !getStorageData().getString(uuidkey + ".name").equalsIgnoreCase(name) ) continue;

                user = KingdomUser.load(getStorageData().getConfigurationSection(uuidkey), uuidkey);
                break;
            }
        }

        if ( user == null ) return null;
        return fixUser(user);
    }

    // for loading purposes
    private KingdomUser getOfflineUser(Player player) {
        KingdomUser user = null;

        // search by uuid
        if ( getStorageData().contains(player.getUniqueId().toString()) ) {
            user = KingdomUser.load(getStorageData().getConfigurationSection(player.getUniqueId().toString()), player);
        }

        // uuid is null or has no results, search by name
        if ( user == null ) {
            for (String uuidkey : getStorageData().getKeys(false)) {
                if ( !getStorageData().contains(uuidkey + ".name") ) continue;
                if ( !getStorageData().getString(uuidkey + ".name").equalsIgnoreCase(player.getName()) ) continue;

                user = KingdomUser.load(getStorageData().getConfigurationSection(uuidkey), player);
                break;
            }
        }

        if ( user == null ) return null;
        return fixUser(user);
    }

    @Override
    public KingdomUser loadUser(Player player) {
        if ( getUser(player) != null ) return getUser(player);

        KingdomUser user = getOfflineUser(player);

        if ( user == null ) {
            ConfigurationSection cs = getStorageData().createSection(player.getUniqueId().toString());
            user = KingdomUser.load(cs, player);
        }

        // update name if wrong
        if ( !user.getName().equals(player.getName()) ) {
            getStorageData().set(user.getUuid() + ".name", player.getName());
            user = KingdomUser.load(getStorageData().getConfigurationSection(user.getUuid()), user.getUuid());
            fixUser(user);
        }

        // update uuid if wrong
        if ( !user.getUuid().equals(player.getUniqueId().toString()) ) {
            getStorageData().set(player.getUniqueId().toString(), getStorageData().get(user.getUuid()));
            getStorageData().set(user.getUuid(), null);
            user = KingdomUser.load(getStorageData().getConfigurationSection(player.getUniqueId().toString()), player.getUniqueId().toString());
            fixUser(user);
        }

        users.add(user);

        plugin.getPermissionManager().refresh(user);
        plugin.getLogger().info(user.getName() + " loaded (uuid = " + user.getUuid() + ")");
        return user;
    }

    @Override
    public List<KingdomUser> getAllUsers() {
        List<KingdomUser> users = new ArrayList<>();

        for ( String uuid : getStorageData().getKeys(false) ) {
            users.add(getOfflineUser(UUID.fromString(uuid), null));
        }

        return users;
    }

    @Override
    public Kingdom getKingdom(KingdomUser user) {
        return plugin.getApi().getKingdomHandler().getKingdom(user.getKingdom());
    }

    @Override
    public KingdomRank getRank(KingdomUser user) {
        Kingdom ko = plugin.getApi().getKingdomHandler().getKingdom(user.getKingdom());
        if ( ko == null ) return null;

        return ko.getRank(user.getRank());
    }

    @Override
    public void setKingdom(KingdomUser user, Kingdom kingdom) {
        if ( kingdom == null ) {
            Kingdom oldKingdom = getKingdom(user);
            KingdomRank oldRank = getRank(user);

            user.setKingdom(null);
            user.setRank(null);
            user.resetChannels();

            Bukkit.getPluginManager().callEvent(new KingdomLeaveEvent(user, oldKingdom, oldRank));

            save(user);
            return;
        }

        user.setKingdom(kingdom.getName());
        setDefaultRank(user);

        user.delKingdomInvite(kingdom.getName());

        plugin.getPermissionManager().refresh(user);

        Bukkit.getPluginManager().callEvent(new KingdomJoinEvent(user));

        save(user);
    }

    @Override
    public void setRank(KingdomUser user, KingdomRank rank) {
        Kingdom ko = getKingdom(user);
        if ( ko == null || !ko.hasRank(rank) ) return;

        user.setRank(rank.getName());
        plugin.getPermissionManager().refresh(user);
    }

    @Override
    public void setDefaultRank(KingdomUser user) {
        Kingdom kingdom = getKingdom(user);
        if ( kingdom == null  ) return;

        user.setRank(kingdom.getDefaultRank().getName());
    }

    @Override
    public void save(KingdomUser user) {
        user.save(getStorageData().getConfigurationSection(user.getUuid()));
        save();
    }

}
