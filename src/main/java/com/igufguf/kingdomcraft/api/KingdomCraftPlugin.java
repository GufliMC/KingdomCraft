package com.igufguf.kingdomcraft.api;

import com.igufguf.kingdomcraft.api.handlers.KingdomManager;
import com.igufguf.kingdomcraft.api.handlers.PlayerManager;
import com.igufguf.kingdomcraft.api.scheduler.AbstractScheduler;

public interface KingdomCraftPlugin {

    AbstractScheduler getScheduler();

    PlayerManager getPlayerManager();

    KingdomManager getKingdomManager();

}
