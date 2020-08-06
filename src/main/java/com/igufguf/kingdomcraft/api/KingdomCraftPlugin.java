package com.igufguf.kingdomcraft.api;

import com.igufguf.kingdomcraft.api.managers.CommandManager;
import com.igufguf.kingdomcraft.api.managers.KingdomManager;
import com.igufguf.kingdomcraft.api.managers.MessageManager;
import com.igufguf.kingdomcraft.api.managers.PlayerManager;
import com.igufguf.kingdomcraft.api.scheduler.AbstractScheduler;

import java.io.InputStream;

public interface KingdomCraftPlugin {

    AbstractScheduler getScheduler();

    PlayerManager getPlayerManager();

    KingdomManager getKingdomManager();

    MessageManager getMessageManager();

    CommandManager getCommandManager();
}
