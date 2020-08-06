package com.igufguf.kingdomcraft.common.storage;

import com.igufguf.kingdomcraft.api.KingdomCraftPlugin;
import com.igufguf.kingdomcraft.api.models.Kingdom;
import com.igufguf.kingdomcraft.api.models.Player;
import com.igufguf.kingdomcraft.api.models.Rank;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class Storage {

    private final KingdomCraftPlugin plugin;
    private final StorageImplementation impl;

    public Storage(KingdomCraftPlugin plugin) {
        this.plugin = plugin;
        this.impl = new DatabaseStorageImplementation();
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

    public CompletableFuture<List<Kingdom>> getKingdoms() {
        return makeFuture(impl::getKingdoms);
    }

    public CompletableFuture<Void> saveKingdom(Kingdom kingdom) {
        return makeFuture(() -> impl.saveKingdom(kingdom));
    }

    public CompletableFuture<Void> saveRank(Rank rank) {
        return makeFuture(() -> impl.saveRank(rank));
    }

    public CompletableFuture<Void> deleteKingdom(Kingdom kingdom) {
        return makeFuture(() -> impl.deleteKingdom(kingdom));
    }

    // ----

    private <T> CompletableFuture<T> makeFuture(Callable<T> supplier) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return supplier.call();
            } catch (Exception e) {
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                }
                throw new CompletionException(e);
            }
        }, this.plugin.getScheduler().async());
    }

    private CompletableFuture<Void> makeFuture(Runnable runnable) {
        return CompletableFuture.runAsync(() -> {
            try {
                runnable.run();
            } catch (Exception e) {
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                }
                throw new CompletionException(e);
            }
        }, this.plugin.getScheduler().async());
    }

}
