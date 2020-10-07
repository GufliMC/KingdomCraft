package com.guflan.kingdomcraft.api;

import com.guflan.kingdomcraft.api.chat.ChatManager;
import com.guflan.kingdomcraft.api.domain.Kingdom;
import com.guflan.kingdomcraft.api.domain.User;
import com.guflan.kingdomcraft.api.entity.Player;
import com.guflan.kingdomcraft.api.placeholders.PlaceholderManager;
import com.guflan.kingdomcraft.api.event.EventManager;
import com.guflan.kingdomcraft.api.command.CommandManager;
import com.guflan.kingdomcraft.api.messages.MessageManager;
import com.guflan.kingdomcraft.api.scheduler.AbstractScheduler;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public interface KingdomCraft {

    AbstractScheduler getScheduler();

    //

    MessageManager getMessageManager();

    CommandManager getCommandManager();

    EventManager getEventManager();

    ChatManager getChatManager();

    PlaceholderManager getPlaceholderManager();

    //

    List<Player> getOnlinePlayers();

    Player getPlayer(UUID uuid);

    User getUser(Player player);

    //

    Set<Kingdom> getKingdoms();

    Kingdom getKingdom(String name);

    Kingdom createKingdom(String name);

    void delete(Kingdom kingdom);

    void save(Kingdom kingdom);

    //

    Set<User> getOnlineUsers();

    CompletableFuture<Set<User>> getUsers();

    CompletableFuture<User> getUser(String name);

    CompletableFuture<User> getUser(UUID uuid);

    void save(User user);

    //

    void log(String msg);

    void log(String msg, Level level);

    void join(Player player);

    void quit(Player player);

}
