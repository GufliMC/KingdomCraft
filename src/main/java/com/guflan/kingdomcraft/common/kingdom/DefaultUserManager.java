package com.guflan.kingdomcraft.common.kingdom;

import com.guflan.kingdomcraft.api.KingdomCraftBridge;
import com.guflan.kingdomcraft.api.entity.Player;
import com.guflan.kingdomcraft.common.storage.Storage;
import com.guflan.kingdomcraft.api.managers.UserManager;
import com.guflan.kingdomcraft.api.domain.Kingdom;
import com.guflan.kingdomcraft.api.domain.User;

import java.util.*;
import java.util.concurrent.ExecutionException;

public class DefaultUserManager implements UserManager {

    private final KingdomCraftBridge bridge;
    private final Storage storage;

    protected final List<User> onlineUsers = new ArrayList<>();

    public DefaultUserManager(KingdomCraftBridge bridge, Storage storage) {
        this.bridge = bridge;
        this.storage = storage;
    }

    public List<User> getOnlineUsers() {
        return new ArrayList<>(onlineUsers);
    }

    @Override
    public List<User> getUsers() {
        try {
            return storage.getPlayers().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    @Override
    public User loadUser(UUID uuid, String name) {
        try {
            User player = storage.getPlayer(uuid).get();
            if ( player == null ) {
                player = storage.getPlayer(name).get();
            }

            if ( player == null ) {
                player = storage.createPlayer(uuid, name).get();
            }
            else if ( !player.getName().equals(name) ) {
                // TODO update name
            }

            return player;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public User getUser(String name) {
        User user = onlineUsers.stream().filter(u -> u.getName().equals(name)).findFirst().orElse(null);
        if ( user != null ) {
            return user;
        }

        try {
            return storage.getPlayer(name).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public User getUser(UUID uuid) {
        User user = onlineUsers.stream().filter(u -> u.getUniqueId() == uuid).findFirst().orElse(null);
        if ( user != null ) {
            return user;
        }

        try {
            return storage.getPlayer(uuid).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void saveUser(User user) {
        storage.savePlayer(user);
    }

    public void join(User user) {
        if ( onlineUsers.contains(user) ) {
            return;
        }

        onlineUsers.add(user);
        bridge.getEventManager().leave(user);
    }

    public void quit(User user) {
        onlineUsers.remove(user);
        bridge.getEventManager().leave(user);
    }

    // -------------

    /*
    public void joinKingdom(User player, Kingdom kingdom) {
        player.setKingdom(kingdom);
        storage.savePlayer(player);
    }

    public void leaveKingdom(User player) {
        player.setKingdom(null);
        storage.savePlayer(player);
    }

    @Override
    public void addInvite(User from, User target) {
        target.addInvite(from);
        savePlayer(target);
    }
     */

}
