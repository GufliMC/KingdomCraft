package com.guflan.kingdomcraft.common.util;

import com.guflan.kingdomcraft.api.entity.PlatformLocation;
import com.guflan.kingdomcraft.api.entity.PlatformPlayer;
import com.guflan.kingdomcraft.common.KingdomCraftImpl;
import com.guflan.kingdomcraft.common.scheduler.SchedulerTask;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class Teleporter {

    private final static String TELEPORT_KEY = "teleporter";

    private static KingdomCraftImpl kdc;
    public static void register(KingdomCraftImpl kdc) {
        Teleporter.kdc = kdc;
    }

    public static CompletableFuture<Void> teleport(PlatformPlayer player, PlatformLocation target, int delay) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        if ( delay != 0 ) {
            kdc.getMessageManager().send(player, "teleport", delay + "");
        }

        SchedulerTask task = kdc.getPlugin().getScheduler().syncLater(() -> {
            player.teleport(target);
            future.complete(null);
        }, delay, TimeUnit.SECONDS);
        player.set(TELEPORT_KEY, task);

        return future;
    }

    public static void cancel(PlatformPlayer player) {
        if ( !player.has(TELEPORT_KEY) ) {
            return;
        }
        kdc.getMessageManager().send(player, "teleportCancel");
        player.get(TELEPORT_KEY, SchedulerTask.class).cancel();
    }

}
