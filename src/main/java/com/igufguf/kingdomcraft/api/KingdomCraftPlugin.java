package com.igufguf.kingdomcraft.api;

import com.igufguf.kingdomcraft.api.chat.ChatManager;
import com.igufguf.kingdomcraft.api.event.EventManager;
import com.igufguf.kingdomcraft.api.integration.Integration;
import com.igufguf.kingdomcraft.api.managers.CommandManager;
import com.igufguf.kingdomcraft.api.managers.KingdomManager;
import com.igufguf.kingdomcraft.api.managers.MessageManager;
import com.igufguf.kingdomcraft.api.managers.PlayerManager;
import com.igufguf.kingdomcraft.api.placeholders.PlaceholderManager;
import com.igufguf.kingdomcraft.api.scheduler.AbstractScheduler;

import java.io.InputStream;

public interface KingdomCraftPlugin {

    AbstractScheduler getScheduler();

    Integration getIntegration();

    PlayerManager getPlayerManager();

    KingdomManager getKingdomManager();

    MessageManager getMessageManager();

    CommandManager getCommandManager();

    EventManager getEventManager();

    ChatManager getChatManager();

    PlaceholderManager getPlaceholderManager();

}
