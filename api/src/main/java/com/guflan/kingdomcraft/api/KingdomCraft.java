package com.guflan.kingdomcraft.api;

import com.guflan.kingdomcraft.api.chat.ChatManager;
import com.guflan.kingdomcraft.api.command.CommandManager;
import com.guflan.kingdomcraft.api.domain.*;
import com.guflan.kingdomcraft.api.entity.PlatformPlayer;
import com.guflan.kingdomcraft.api.event.EventManager;
import com.guflan.kingdomcraft.api.messages.MessageManager;
import com.guflan.kingdomcraft.api.placeholders.PlaceholderManager;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface KingdomCraft {

    // managers

    MessageManager getMessageManager();

    CommandManager getCommandManager();

    EventManager getEventManager();

    ChatManager getChatManager();

    PlaceholderManager getPlaceholderManager();

    // players

    List<PlatformPlayer> getOnlinePlayers();

    PlatformPlayer getPlayer(UUID uuid);

    PlatformPlayer getPlayer(User user);

    User getUser(PlatformPlayer player);

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

}
