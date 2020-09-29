package com.guflan.kingdomcraft.common.storage;

import com.guflan.kingdomcraft.api.KingdomCraftPlugin;
import com.guflan.kingdomcraft.api.domain.Kingdom;
import com.guflan.kingdomcraft.api.domain.Player;
import com.guflan.kingdomcraft.api.domain.Rank;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class Storage {

    private final KingdomCraftPlugin plugin;
    private final StorageImplementation impl;


    public Storage(KingdomCraftPlugin plugin, StorageImplementation implementation) {
        this.plugin = plugin;
        this.impl = implementation;
    }

    // ----

    public CompletableFuture<List<Player>> getPlayers() {
        return makeFuture(impl::getPlayers);
    }

    public CompletableFuture<Player> getPlayer(String name) {
        return makeFuture(() -> impl.getPlayer(name));
    }

    public CompletableFuture<Player> getPlayer(UUID uuid) {
        return makeFuture(() -> impl.getPlayer(uuid));
    }

    public CompletableFuture<Void> savePlayer(Player player) {
        return makeFuture(() -> impl.savePlayer(player));
    }

    public CompletableFuture<Player> createPlayer(UUID uuid, String name) {
        return makeFuture(() -> impl.createPlayer(uuid, name));
    }

    public CompletableFuture<List<Kingdom>> getKingdoms() {
        return makeFuture(impl::getKingdoms);
    }

    public CompletableFuture<Void> saveKingdom(Kingdom kingdom) {
        return makeFuture(() -> impl.saveKingdom(kingdom));
    }

    public CompletableFuture<Kingdom> createKingdom(String name) {
        return makeFuture(() -> impl.createKingdom(name));
    }

    public CompletableFuture<Void> deleteKingdom(Kingdom kingdom) {
        return makeFuture(() -> impl.deleteKingdom(kingdom));
    }

    public CompletableFuture<Void> saveRank(Rank rank) {
        return makeFuture(() -> impl.saveRank(rank));
    }

    public CompletableFuture<Rank> createRank(String name, Kingdom kingdom) {
        return makeFuture(() -> impl.createRank(name, kingdom));
    }

    public CompletableFuture<Void> deleteRank(Rank rank) {
        return makeFuture(() -> impl.deleteRank(rank));
    }

    // ----

    private <T> CompletableFuture<T> makeFuture(Callable<T> supplier) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return supplier.call();
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        }, this.plugin.getScheduler().async());
    }

    private CompletableFuture<Void> makeFuture(Runnable runnable) {
        return CompletableFuture.runAsync(() -> {
            try {
                runnable.run();
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        }, this.plugin.getScheduler().async());
    }

}
