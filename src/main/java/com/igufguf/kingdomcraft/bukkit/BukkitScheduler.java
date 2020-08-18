package com.igufguf.kingdomcraft.bukkit;

import com.igufguf.kingdomcraft.api.scheduler.AbstractScheduler;

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
