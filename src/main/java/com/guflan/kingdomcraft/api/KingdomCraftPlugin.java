package com.guflan.kingdomcraft.api;

import com.guflan.kingdomcraft.api.scheduler.AbstractScheduler;

import java.util.logging.Level;

public interface KingdomCraftPlugin {

    AbstractScheduler getScheduler();

    void log(String msg);

    void log(String msg, Level level);

}
