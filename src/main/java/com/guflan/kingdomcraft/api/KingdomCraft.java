package com.guflan.kingdomcraft.api;

import com.guflan.kingdomcraft.api.chat.ChatManager;
import com.guflan.kingdomcraft.api.command.CommandManager;
import com.guflan.kingdomcraft.api.domain.models.*;
import com.guflan.kingdomcraft.api.entity.Player;
import com.guflan.kingdomcraft.api.event.EventManager;
import com.guflan.kingdomcraft.api.messages.MessageManager;
import com.guflan.kingdomcraft.api.placeholders.PlaceholderManager;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public interface KingdomCraft {

    KingdomCraftPlugin getPlugin();

    // managers

    MessageManager getMessageManager();

    CommandManager getCommandManager();

    EventManager getEventManager();

    ChatManager getChatManager();

    PlaceholderManager getPlaceholderManager();

    // players

    List<Player> getOnlinePlayers();

    Player getPlayer(UUID uuid);

    Player getPlayer(User user);

    User getUser(Player player);

    // kingdoms

    List<Kingdom> getKingdoms();

    Kingdom getKingdom(String name);

    Kingdom createKingdom(String name);

    CompletableFuture<Void> delete(Kingdom kingdom);

    CompletableFuture<Void> save(Kingdom kingdom);

    // ranks

    CompletableFuture<Void> delete(Rank rank);

    CompletableFuture<Void> save(Rank rank);

    // relations

    List<Relation> getRelations(Kingdom kingdom);

    void setRelation(Kingdom kingdom, Kingdom other, RelationType type);

    Relation getRelation(Kingdom kingdom, Kingdom other);

    void addRelationRequest(Kingdom kingdom, Kingdom other, RelationType type);

    Relation getRelationRequest(Kingdom kingdom, Kingdom other);

    void removeRelationRequest(Kingdom kingdom, Kingdom other);

    // users

    List<User> getOnlineUsers();

    User getOnlineUser(String name);

    User getOnlineUser(UUID uuid);

    CompletableFuture<List<User>> getUsers();

    CompletableFuture<User> getUser(String name);

    CompletableFuture<User> getUser(UUID uuid);

    CompletableFuture<Void> save(User user);

    //

    void join(Player player);

    void quit(Player player);

}
