package com.guflan.kingdomcraft.common.ebean;

import com.guflan.kingdomcraft.api.KingdomCraftPlugin;
import com.guflan.kingdomcraft.api.domain.DomainManager;
import com.guflan.kingdomcraft.api.domain.models.*;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class EBeanManager implements DomainManager  {

    private final EBeanStorage storage;
    private final EBeanCache cache;
    private final EBeanFactory factory;

    public EBeanManager(KingdomCraftPlugin plugin) {
        this.storage = new EBeanStorage(plugin);
        this.cache = new EBeanCache();
        this.factory = new EBeanFactory();

        storage.getKingdoms().thenAccept(kingdoms -> kingdoms.forEach(cache::watch));
        storage.getRelations().thenAccept(relations -> relations.forEach(cache::watch));
    }

    public boolean init(String url, String driver, String username, String password) {
        return this.storage.init(url, driver, username, password);
    }

    @Override
    public List<Kingdom> getCachedKingdoms() {
        return cache.getKingdoms();
    }

    @Override
    public Kingdom getCachedKingdom(String name) {
        return cache.getKingdom(name);
    }

    @Override
    public CompletableFuture<Void> delete(Kingdom kingdom) {
        cache.unwatch(kingdom);
        return storage.delete(kingdom);
    }

    @Override
    public CompletableFuture<Void> save(Kingdom kingdom) {
        return storage.save(kingdom);
    }

    @Override
    public Kingdom createKingdom(String name) {
        Kingdom kingdom = factory.createKingdom(name);
        cache.watch(kingdom);
        return kingdom;
    }

    // ranks

    @Override
    public CompletableFuture<Void> delete(Rank rank) {
        return storage.delete(rank);
    }

    @Override
    public CompletableFuture<Void> save(Rank rank) {
        return storage.save(rank);
    }

    // relations

    @Override
    public List<Relation> getRelations(Kingdom kingdom) {
        return cache.getRelations(kingdom);
    }

    @Override
    public void setRelation(Kingdom kingdom, Kingdom other, RelationType type) {
        Relation rel = getRelation(kingdom, other);
        if ( rel != null ) {
            storage.delete(rel);
            cache.unwatch(rel);
            return;
        }

        rel = factory.createRelation(kingdom, other, type, false);
        storage.save(rel);
        cache.watch(rel);
    }

    @Override
    public Relation getRelation(Kingdom kingdom, Kingdom other) {
        return cache.getRelations(kingdom, other).stream().filter(r -> !r.isRequest()).findFirst().orElse(null);
    }

    @Override
    public void addRelationRequest(Kingdom kingdom, Kingdom other, RelationType type) {
        Relation rel = factory.createRelation(kingdom, other, type, true);
        storage.save(rel);
        cache.watch(rel);
    }

    @Override
    public Relation getRelationRequest(Kingdom kingdom, Kingdom other) {
        return cache.getRelations(kingdom, other).stream().filter(Relation::isRequest).findFirst().orElse(null);
    }

    @Override
    public void removeRelationRequest(Kingdom kingdom, Kingdom other) {
        Relation rel = getRelationRequest(kingdom, other);
        if ( rel == null ) {
            return;
        }

        storage.delete(rel);
        cache.unwatch(rel);
    }

    // users

    @Override
    public List<User> getCachedUsers() {
        return cache.getUsers();
    }

    @Override
    public User getCachedUser(String name) {
        return cache.getUser(name);
    }

    @Override
    public User getCachedUser(UUID uuid) {
        return cache.getUser(uuid);
    }

    @Override
    public CompletableFuture<List<User>> getUsers() {
        return storage.getUsers().thenApply(users -> {
            users.forEach(cache::fit);
            return users;
        });
    }

    @Override
    public CompletableFuture<User> getUser(String name) {
        User user = getCachedUser(name);
        if ( user != null ) {
            return CompletableFuture.completedFuture(user);
        }

        return storage.getUser(name).thenApply(u -> {
            cache.fit(u);
            return u;
        });
    }

    @Override
    public CompletableFuture<User> getUser(UUID uuid) {
        User user = getCachedUser(uuid);
        if ( user != null ) {
            return CompletableFuture.completedFuture(user);
        }

        return storage.getUser(uuid).thenApply(u -> {
            cache.fit(u);
            return u;
        });
    }

    @Override
    public CompletableFuture<Void> delete(User user) {
        return storage.delete(user);
    }

    @Override
    public CompletableFuture<Void> save(User user) {
        return storage.save(user);
    }

    @Override
    public User createUser(UUID uuid, String name) {
        return factory.createUser(uuid, name);
    }

    @Override
    public void addCachedUser(User user) {
        cache.watch(user);
    }

    @Override
    public void removeCachedUser(User user) {
        cache.unwatch(user);
    }
}
