package com.guflan.kingdomcraft.bukkit.bridge;

import com.guflan.kingdomcraft.api.scheduler.AbstractScheduler;
import org.bukkit.plugin.Plugin;

import java.util.concurrent.Executor;

public class BukkitScheduler extends AbstractScheduler  {

    private final Executor sync;

    public BukkitScheduler(Plugin plugin) {
        this.sync = r -> plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, r);
    }

    @Override
    public Executor sync() {
        return this.sync;
    }
}
