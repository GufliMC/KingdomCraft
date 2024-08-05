/*
 * This file is part of KingdomCraft.
 *
 * KingdomCraft is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * KingdomCraft is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with KingdomCraft. If not, see <https://www.gnu.org/licenses/>.
 */

package com.gufli.kingdomcraft.common.util;

import com.gufli.kingdomcraft.api.KingdomCraftProvider;
import com.gufli.kingdomcraft.api.entity.PlatformLocation;
import com.gufli.kingdomcraft.api.entity.PlatformPlayer;
import com.gufli.kingdomcraft.common.KingdomCraftPlugin;
import com.gufli.kingdomcraft.common.scheduler.AbstractScheduler;
import com.gufli.kingdomcraft.common.scheduler.SchedulerTask;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class Teleporter {

    private final static String TELEPORT_KEY = "teleporter";

    private static AbstractScheduler scheduler;

    public static void register(AbstractScheduler scheduler) {
        Teleporter.scheduler = scheduler;
    }

    public static CompletableFuture<Void> teleport(PlatformPlayer player, PlatformLocation target) {
        int delay = 0;
        if ( !player.isAdmin() || !player.hasPermission("kingdom.admin.teleportdelay") ) {
            delay = KingdomCraftProvider.get().getConfig().getTeleportDelay();
        }

        return teleport(player, target, delay);
    }

    public static CompletableFuture<Void> teleport(PlatformPlayer player, PlatformLocation target, int delay) {
        var teleportTask = player.get(TELEPORT_KEY, SchedulerTask.class);
        if ( teleportTask != null ) {
            teleportTask.cancel();
            teleportTask = null;
        }

        if ( delay <= 0 ) {
            player.teleport(target);
            return CompletableFuture.completedFuture(null);
        }

        CompletableFuture<Void> future = new CompletableFuture<>();
        KingdomCraftProvider.get().getMessages().send(player, "teleport", delay + "");

        player.set(TELEPORT_KEY, scheduler.syncLater(() -> {
            player.remove(TELEPORT_KEY);
            player.teleport(target);
            future.complete(null);
        }, delay, TimeUnit.SECONDS));

        return future;
    }

    public static void cancel(PlatformPlayer player) {
        if ( player == null || !player.has(TELEPORT_KEY) ) {
            return;
        }
        KingdomCraftProvider.get().getMessages().send(player, "teleportCancel");
        player.get(TELEPORT_KEY, SchedulerTask.class).cancel();
        player.remove(TELEPORT_KEY);
    }

}