package com.guflan.kingdomcraft.api.domain;

import com.guflan.kingdomcraft.api.domain.models.*;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public interface DomainManager {

    // kingdoms

    List<Kingdom> getCachedKingdoms();

    Kingdom getCachedKingdom(String name);

    CompletableFuture<Void> delete(Kingdom kingdom);

    CompletableFuture<Void> save(Kingdom kingdom);

    Kingdom createKingdom(String name);

    // ranks

    CompletableFuture<Void> delete(Rank rank);

    CompletableFuture<Void> save(Rank rank);

    // relations

    List<Relation> getRelations(Kingdom kingdom);

    void setRelation(Kingdom kingdom, Kingdom other, RelationType type);

    Relation getRelation(Kingdom kingdom, Kingdom other);

    void addRelationRequest(Kingdom kingdom, Kingdom other, RelationType type);

    Relation getRelationRequest(Kingdom kingdom, Kingdom other);

    void removeRelationRequest(Kingdom kingdom, Kingdom other);

    // users

    List<User> getCachedUsers();

    User getCachedUser(String name);

    User getCachedUser(UUID uuid);

    CompletableFuture<List<User>> getUsers();

    CompletableFuture<User> getUser(String name);

    CompletableFuture<User> getUser(UUID uuid);

    CompletableFuture<Void> delete(User user);

    CompletableFuture<Void> save(User user);

    User createUser(UUID uuid, String name);

    // cache

    void addCachedUser(User user);

    void removeCachedUser(User user);

}
