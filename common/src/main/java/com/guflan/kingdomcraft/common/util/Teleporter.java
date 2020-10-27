/*
 * This file is part of KingdomCraft.
 *
 * KingdomCraft is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * KingdomCraft is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with KingdomCraft. If not, see <https://www.gnu.org/licenses/>.
 */

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

    public static CompletableFuture<Void> teleport(PlatformPlayer player, PlatformLocation target) {
        int delay = 0;
        if ( !player.isAdmin() ) {
            delay = kdc.getConfig().getTeleportDelay();
        }

        return teleport(player, target, delay);
    }

    public static CompletableFuture<Void> teleport(PlatformPlayer player, PlatformLocation target, int delay) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        if ( delay != 0 ) {
            kdc.getMessageManager().send(player, "teleport", delay + "");
        }

        player.set(TELEPORT_KEY, kdc.getPlugin().getScheduler().syncLater(() -> {
            player.remove(TELEPORT_KEY);
            player.teleport(target);
            future.complete(null);
        }, delay, TimeUnit.SECONDS));

        return future;
    }

    public static void cancel(PlatformPlayer player) {
        if ( !player.has(TELEPORT_KEY) ) {
            return;
        }
        kdc.getMessageManager().send(player, "teleportCancel");
        player.get(TELEPORT_KEY, SchedulerTask.class).cancel();
        player.remove(TELEPORT_KEY);
    }

}
