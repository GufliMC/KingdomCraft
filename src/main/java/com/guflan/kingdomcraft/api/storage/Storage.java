package com.guflan.kingdomcraft.api.storage;

import com.guflan.kingdomcraft.api.domain.Kingdom;
import com.guflan.kingdomcraft.api.domain.User;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface Storage {

    CompletableFuture<Set<Kingdom>> getKingdoms();

    Kingdom createKingdom(String name);

    CompletableFuture<Void> delete(Kingdom kingdom);

    CompletableFuture<Void> save(Kingdom kingdom);

    // users

    CompletableFuture<Set<User>> getUsers();

    User createUser(UUID uuid, String name);

    CompletableFuture<User> getUser(String name);

    CompletableFuture<User> getUser(UUID uuid);

    CompletableFuture<Void> save(User user);

}
