package com.igufguf.kingdomcraft.handlers;

import com.igufguf.kingdomcraft.KingdomCraft;
import com.igufguf.kingdomcraft.api.events.KingdomJoinEvent;
import com.igufguf.kingdomcraft.api.events.KingdomLeaveEvent;
import com.igufguf.kingdomcraft.api.exceptions.UserNotFoundException;
import com.igufguf.kingdomcraft.api.handlers.KingdomUserHandler;
import com.igufguf.kingdomcraft.api.models.database.StorageManager;
import com.igufguf.kingdomcraft.api.models.kingdom.Kingdom;
import com.igufguf.kingdomcraft.api.models.kingdom.KingdomRank;
import com.igufguf.kingdomcraft.api.models.kingdom.KingdomUser;
import org.bukkit.Bukkit;
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
public class SimpleUserHandler extends StorageManager implements KingdomUserHandler {

    private final KingdomCraft plugin;

    private final List<KingdomUser> users = Collections.synchronizedList(new ArrayList<>());

    public SimpleUserHandler(KingdomCraft plugin) {
        super(new File(plugin.getDataFolder() + "/data", "users.data"));
        this.plugin = plugin;
    }

    @Override
    public void registerUser(KingdomUser user) {
        if ( !users.contains(user) ) users.add(user);
        plugin.getPermissionManager().refresh(user);

        plugin.getLogger().info(user.getName() + " registered (uuid = " + user.getUuid() + ")");
    }

    @Override
    public void unregisterUser(KingdomUser user) {
        users.remove(user);
        plugin.getPermissionManager().clear(user);
    }

    @Override
    public List<KingdomUser> getUsers() {
        return new ArrayList<>(users);
    }

    @Override
    public KingdomUser getUser(Player p) {
        if ( p == null || !p.isOnline() ) return (p != null ? getOfflineUser(p.getUniqueId().toString()) : null);
        for ( KingdomUser ku : users ) {
            if ( ku.getPlayer() == p ) return ku;
        }
        return null;
    }

    @Override
    public KingdomUser getUser(String username) {
        Player p = Bukkit.getPlayerExact(username);
        if ( p != null ) return getUser(p);

        return getOfflineUser(username);
    }

    @Override
    public KingdomUser getOfflineUser(String uuid, String name) {

        // uuid exists in data
        if ( uuid != null && getStorageData().contains(uuid) ) {
            if ( !getStorageData().getString(uuid + ".name").equalsIgnoreCase(name) ) getStorageData().set(uuid + ".name", name); // update name if changed
            return getOfflineUser(uuid);
        }

        // uuid doesn't exists but name does
        for ( String key : getStorageData().getKeys(false) ) {
            if ( !getStorageData().getString(key + ".name").equalsIgnoreCase(name) ) continue;
            return getOfflineUser(key);
        }

        // create new user
        if ( uuid == null ) return null;
        if ( !getStorageData().contains(uuid) ) getStorageData().createSection(uuid);
        return new KingdomUser(uuid, name);
    }

    @Override
    public KingdomUser getOfflineUser(String uuid) throws UserNotFoundException {
        if ( !getStorageData().contains(uuid) ) throw new UserNotFoundException(uuid);

        KingdomUser user = KingdomUser.load(getStorageData().getConfigurationSection(uuid), uuid);

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
    public List<KingdomUser> getAllUsers() {
        List<KingdomUser> users = new ArrayList<>();

        for ( String uuid : getStorageData().getKeys(false) ) {
            users.add(getOfflineUser(uuid));
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
