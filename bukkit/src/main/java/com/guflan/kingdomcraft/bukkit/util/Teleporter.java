package com.guflan.kingdomcraft.bukkit.util;

import com.guflan.kingdomcraft.api.entity.PlatformPlayer;
import com.guflan.kingdomcraft.bukkit.KingdomCraftBukkitPlugin;
import com.guflan.kingdomcraft.common.scheduler.SchedulerTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class Teleporter {

    private static String TELEPORT_KEY = "teleporter";

    private static KingdomCraftBukkitPlugin plugin;
    public static void register(KingdomCraftBukkitPlugin plugin) {
        Teleporter.plugin = plugin;
    }

    public static CompletableFuture<Void> teleport(PlatformPlayer player, Location target, int delay) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        if ( delay != 0 ) {
            plugin.getKdc().getMessageManager().send(player, "teleporting", delay + "");
        }

        SchedulerTask task = plugin.getScheduler().syncLater(() -> {
            Player bplayer = Bukkit.getPlayer(player.getUniqueId());
            if ( bplayer == null ) {
                return;
            }

            bplayer.teleport(target);
            future.complete(null);
        }, delay, TimeUnit.SECONDS);
        player.set(TELEPORT_KEY, task);

        return future;
    }

    public static void cancel(PlatformPlayer player) {
        if ( !player.has(TELEPORT_KEY) ) {
            return;
        }
        player.get(TELEPORT_KEY, SchedulerTask.class).cancel();
    }

}
