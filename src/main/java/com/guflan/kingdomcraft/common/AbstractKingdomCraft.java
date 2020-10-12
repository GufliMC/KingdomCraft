package com.guflan.kingdomcraft.common;

import com.guflan.kingdomcraft.api.KingdomCraft;
import com.guflan.kingdomcraft.api.chat.ChatManager;
import com.guflan.kingdomcraft.api.command.CommandManager;
import com.guflan.kingdomcraft.api.domain.DomainContext;
import com.guflan.kingdomcraft.api.domain.models.*;
import com.guflan.kingdomcraft.api.entity.Player;
import com.guflan.kingdomcraft.api.event.EventManager;
import com.guflan.kingdomcraft.api.placeholders.PlaceholderManager;
import com.guflan.kingdomcraft.common.chat.BasicChatManager;
import com.guflan.kingdomcraft.common.command.DefaultCommandManager;
import com.guflan.kingdomcraft.common.event.DefaultEventManager;
import com.guflan.kingdomcraft.common.placeholders.DefaultPlaceholderManager;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public abstract class AbstractKingdomCraft implements KingdomCraft {

    private final DefaultCommandManager commandManager;
    private final DefaultEventManager eventManager;
    private final BasicChatManager chatManager;
    private final DefaultPlaceholderManager placeholderManager;

    //

    private final DomainContext context;

    public AbstractKingdomCraft(DomainContext context) {
        this.context = context;

        this.commandManager = new DefaultCommandManager(this);
        this.eventManager = new DefaultEventManager();
        this.chatManager = new BasicChatManager(this);
        this.placeholderManager = new DefaultPlaceholderManager(this);
    }

    //

    @Override
    public CommandManager getCommandManager() {
        return commandManager;
    }

    @Override
    public EventManager getEventManager() {
        return eventManager;
    }

    @Override
    public ChatManager getChatManager() {
        return chatManager;
    }

    @Override
    public PlaceholderManager getPlaceholderManager() {
        return placeholderManager;
    }

    //

    @Override
    public void join(Player player) {
        getPlugin().getScheduler().async().execute(() -> {
            try {
                User user = getUser(player.getUniqueId()).get();
                if ( user == null ) {
                    user = getUser(player.getName()).get();
                }

                if ( user == null ) {
                    user = context.createUser(player.getUniqueId(), player.getName());
                }

                context.addCachedUser(user);

                // TODO join event
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void quit(Player player) {
        User user = context.getCachedUser(player.getUniqueId());
        context.removeCachedUser(user);
    }

    // kingdoms

    @Override
    public List<Kingdom> getKingdoms() {
        return context.getCachedKingdoms();
    }

    @Override
    public Kingdom getKingdom(String name) {
        return context.getCachedKingdom(name);
    }

    @Override
    public Kingdom createKingdom(String name) {
        Kingdom kingdom = context.createKingdom(name);
        context.save(kingdom);
        eventManager.kingdomCreate(kingdom);
        return kingdom;
    }

    @Override
    public CompletableFuture<Void> delete(Kingdom kingdom) {
        return context.delete(kingdom);
    }

    @Override
    public CompletableFuture<Void> save(Kingdom kingdom) {
        return context.save(kingdom);
    }

    // ranks

    @Override
    public CompletableFuture<Void> delete(Rank rank) {
        return context.delete(rank);
    }

    @Override
    public CompletableFuture<Void> save(Rank rank) {
        return context.save(rank);
    }


    // relations

    @Override
    public List<Relation> getRelations(Kingdom kingdom) {
        return context.getRelations(kingdom);
    }

    @Override
    public void setRelation(Kingdom kingdom, Kingdom other, RelationType type) {
        context.setRelation(kingdom, other, type);
    }

    @Override
    public Relation getRelation(Kingdom kingdom, Kingdom other) {
        return context.getRelation(kingdom, other);
    }

    @Override
    public void addRelationRequest(Kingdom kingdom, Kingdom other, RelationType type) {
        context.addRelationRequest(kingdom, other, type);
    }

    @Override
    public Relation getRelationRequest(Kingdom kingdom, Kingdom other) {
        return context.getRelationRequest(kingdom, other);
    }

    @Override
    public void removeRelationRequest(Kingdom kingdom, Kingdom other) {
        context.removeRelationRequest(kingdom, other);
    }

    // users

    @Override
    public List<User> getOnlineUsers() {
        return context.getCachedUsers();
    }

    @Override
    public User getOnlineUser(String name) {
        return context.getCachedUser(name);
    }

    @Override
    public User getOnlineUser(UUID uuid) {
        return context.getCachedUser(uuid);
    }

    @Override
    public CompletableFuture<List<User>> getUsers() {
        return context.getUsers();
    }

    @Override
    public CompletableFuture<User> getUser(String name) {
        return context.getUser(name);
    }

    @Override
    public CompletableFuture<User> getUser(UUID uuid) {
        return context.getUser(uuid);
    }

    @Override
    public CompletableFuture<Void> save(User user) {
        return context.save(user);
    }

    @Override
    public User getUser(Player player) {
        return getOnlineUser(player.getUniqueId());
    }
}
