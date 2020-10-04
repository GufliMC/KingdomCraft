package com.guflan.kingdomcraft.api;

import com.guflan.kingdomcraft.api.chat.ChatManager;
import com.guflan.kingdomcraft.api.domain.User;
import com.guflan.kingdomcraft.api.entity.Player;
import com.guflan.kingdomcraft.api.placeholders.PlaceholderManager;
import com.guflan.kingdomcraft.api.event.EventManager;
import com.guflan.kingdomcraft.api.managers.CommandManager;
import com.guflan.kingdomcraft.api.managers.KingdomManager;
import com.guflan.kingdomcraft.api.managers.MessageManager;
import com.guflan.kingdomcraft.api.managers.UserManager;
import com.guflan.kingdomcraft.api.scheduler.AbstractScheduler;

import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public interface KingdomCraftBridge {

    AbstractScheduler getScheduler();

    UserManager getUserManager();

    KingdomManager getKingdomManager();

    MessageManager getMessageManager();

    CommandManager getCommandManager();

    EventManager getEventManager();

    ChatManager getChatManager();

    PlaceholderManager getPlaceholderManager();

    List<Player> getOnlinePlayers();

    Player getPlayer(UUID uuid);

    void log(String msg);

    void log(String msg, Level level);

    void join(User user);

    void quit(User user);

}
