package com.guflan.kingdomcraft.bukkit.bridge;

import com.guflan.kingdomcraft.api.scheduler.AbstractScheduler;
import com.guflan.kingdomcraft.bukkit.KingdomCraft;

import java.util.concurrent.Executor;

public class BukkitScheduler extends AbstractScheduler  {

    private final KingdomCraft plugin;
    private final Executor sync;

    public BukkitScheduler(KingdomCraft plugin) {
        this.plugin = plugin;
        this.sync = r -> plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, r);
    }

    @Override
    public Executor sync() {
        return this.sync;
    }
}
