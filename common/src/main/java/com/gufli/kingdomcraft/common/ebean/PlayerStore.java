package com.gufli.kingdomcraft.common.ebean;

import com.gufli.kingdomcraft.api.domain.User;
import com.gufli.kingdomcraft.api.entity.PlatformPlayer;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerStore {

    private final Map<UUID, User> usersByUuid = new ConcurrentHashMap<>();
    private final Map<String, User> usersByName = new ConcurrentHashMap<>();

    private final Map<UUID, PlatformPlayer> playersByUuid = new ConcurrentHashMap<>();
    private final Map<String, PlatformPlayer> playersByName = new ConcurrentHashMap<>();

    public final User getUser(UUID uuid) {
        return usersByUuid.get(uuid);
    }

    public final User getUser(String name) {
        return usersByName.get(name);
    }

    public final User getUser(PlatformPlayer player) {
        return usersByUuid.get(player.getUniqueId());
    }

    public final PlatformPlayer getPlayer(UUID uuid) {
        return playersByUuid.get(uuid);
    }

    public final PlatformPlayer getPlayer(String name) {
        return playersByName.get(name);
    }

    public final PlatformPlayer getPlayer(User user) {
        return playersByUuid.get(user.getUniqueId());
    }

    public final Collection<PlatformPlayer> getPlayers() {
        return playersByUuid.values();
    }

    public final Collection<User> getUsers() {
        return usersByUuid.values();
    }

    public final void remove(UUID uuid) {
        User user = usersByUuid.remove(uuid);
        if ( user != null ) {
            usersByName.remove(user.getName());
        }

        PlatformPlayer player = playersByUuid.remove(uuid);
        if ( player != null ) {
            playersByName.remove(player.getName());
        }
    }

    public final void add(User user, PlatformPlayer player) {
        usersByUuid.put(user.getUniqueId(), user);
        usersByName.put(user.getName(), user);

        playersByUuid.put(user.getUniqueId(), player);
        playersByName.put(user.getName(), player);
    }

}
