package com.guflan.kingdomcraft.api.domain;

import com.guflan.kingdomcraft.api.domain.models.Kingdom;
import com.guflan.kingdomcraft.api.domain.models.Rank;
import com.guflan.kingdomcraft.api.domain.models.Relation;
import com.guflan.kingdomcraft.api.domain.models.User;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface DomainStorage {

    // kingdoms

    CompletableFuture<List<Kingdom>> getKingdoms();

    CompletableFuture<Kingdom> getKingdom(String name);

    CompletableFuture<Void> delete(Kingdom kingdom);

    CompletableFuture<Void> save(Kingdom kingdom);

    // ranks

    CompletableFuture<Void> delete(Rank rank);

    CompletableFuture<Void> save(Rank rank);

    // relations

    CompletableFuture<List<Relation>> getRelations();

    CompletableFuture<List<Relation>> getRelations(Kingdom kingdom);

    CompletableFuture<Void> save(Relation relation);

    CompletableFuture<Void> delete(Relation relation);

    // users

    CompletableFuture<List<User>> getUsers();

    CompletableFuture<User> getUser(String name);

    CompletableFuture<User> getUser(UUID uuid);

    CompletableFuture<Void> save(User user);

    CompletableFuture<Void> delete(User user);

}
