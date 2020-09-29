package com.guflan.kingdomcraft.api;

import com.guflan.kingdomcraft.api.chat.ChatManager;
import com.guflan.kingdomcraft.api.placeholders.PlaceholderManager;
import com.guflan.kingdomcraft.api.domain.Factory;
import com.guflan.kingdomcraft.api.event.EventManager;
import com.guflan.kingdomcraft.api.managers.CommandManager;
import com.guflan.kingdomcraft.api.managers.KingdomManager;
import com.guflan.kingdomcraft.api.managers.MessageManager;
import com.guflan.kingdomcraft.api.managers.PlayerManager;
import com.guflan.kingdomcraft.api.scheduler.AbstractScheduler;

public interface KingdomCraftPlugin {

    AbstractScheduler getScheduler();

    PlayerManager getPlayerManager();

    KingdomManager getKingdomManager();

    MessageManager getMessageManager();

    CommandManager getCommandManager();

    EventManager getEventManager();

    ChatManager getChatManager();

    PlaceholderManager getPlaceholderManager();

    String translateColors(String msg);

    String stripColors(String msg);

    void log(String msg);

}
