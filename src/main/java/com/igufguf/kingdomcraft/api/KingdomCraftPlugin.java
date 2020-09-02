package com.igufguf.kingdomcraft.api;

import com.igufguf.kingdomcraft.api.chat.ChatManager;
import com.igufguf.kingdomcraft.api.domain.Factory;
import com.igufguf.kingdomcraft.api.event.EventManager;
import com.igufguf.kingdomcraft.api.managers.CommandManager;
import com.igufguf.kingdomcraft.api.managers.KingdomManager;
import com.igufguf.kingdomcraft.api.managers.MessageManager;
import com.igufguf.kingdomcraft.api.managers.PlayerManager;
import com.igufguf.kingdomcraft.api.placeholders.PlaceholderManager;
import com.igufguf.kingdomcraft.api.scheduler.AbstractScheduler;

public interface KingdomCraftPlugin {

    AbstractScheduler getScheduler();

    Factory getFactory();

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
