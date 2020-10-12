package com.guflan.kingdomcraft.common;

import com.guflan.kingdomcraft.api.KingdomCraft;
import com.guflan.kingdomcraft.api.chat.ChatManager;
import com.guflan.kingdomcraft.api.command.CommandManager;
import com.guflan.kingdomcraft.api.domain.DomainManager;
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
import java.util.concurrent.Future;

public abstract class AbstractKingdomCraft implements KingdomCraft {

    private final DefaultCommandManager commandManager;
    private final DefaultEventManager eventManager;
    private final BasicChatManager chatManager;
    private final DefaultPlaceholderManager placeholderManager;

    //

    private final DomainManager domainManager;

    public AbstractKingdomCraft(DomainManager domainManager) {
        this.domainManager = domainManager;

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
                    user = domainManager.createUser(player.getUniqueId(), player.getName());
                }

                domainManager.addCachedUser(user);

                // TODO join event
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void quit(Player player) {
        User user = domainManager.getCachedUser(player.getUniqueId());
        domainManager.removeCachedUser(user);
    }

    // kingdoms

    @Override
    public List<Kingdom> getKingdoms() {
        return domainManager.getCachedKingdoms();
    }

    @Override
    public Kingdom getKingdom(String name) {
        return domainManager.getCachedKingdom(name);
    }

    @Override
    public Kingdom createKingdom(String name) {
        Kingdom kingdom = domainManager.createKingdom(name);
        domainManager.save(kingdom);
        eventManager.kingdomCreate(kingdom);
        return kingdom;
    }

    @Override
    public CompletableFuture<Void> delete(Kingdom kingdom) {
        return domainManager.delete(kingdom);
    }

    @Override
    public CompletableFuture<Void> save(Kingdom kingdom) {
        return domainManager.save(kingdom);
    }

    // ranks

    @Override
    public CompletableFuture<Void> delete(Rank rank) {
        return domainManager.delete(rank);
    }

    @Override
    public CompletableFuture<Void> save(Rank rank) {
        return domainManager.save(rank);
    }


    // relations

    @Override
    public List<Relation> getRelations(Kingdom kingdom) {
        return domainManager.getRelations(kingdom);
    }

    @Override
    public void setRelation(Kingdom kingdom, Kingdom other, RelationType type) {
        domainManager.setRelation(kingdom, other, type);
    }

    @Override
    public Relation getRelation(Kingdom kingdom, Kingdom other) {
        return domainManager.getRelation(kingdom, other);
    }

    @Override
    public void addRelationRequest(Kingdom kingdom, Kingdom other, RelationType type) {
        domainManager.addRelationRequest(kingdom, other, type);
    }

    @Override
    public Relation getRelationRequest(Kingdom kingdom, Kingdom other) {
        return domainManager.getRelationRequest(kingdom, other);
    }

    @Override
    public void removeRelationRequest(Kingdom kingdom, Kingdom other) {
        domainManager.removeRelationRequest(kingdom, other);
    }

    // users

    @Override
    public List<User> getOnlineUsers() {
        return domainManager.getCachedUsers();
    }

    @Override
    public User getOnlineUser(String name) {
        return domainManager.getCachedUser(name);
    }

    @Override
    public User getOnlineUser(UUID uuid) {
        return domainManager.getCachedUser(uuid);
    }

    @Override
    public CompletableFuture<List<User>> getUsers() {
        return domainManager.getUsers();
    }

    @Override
    public CompletableFuture<User> getUser(String name) {
        return domainManager.getUser(name);
    }

    @Override
    public CompletableFuture<User> getUser(UUID uuid) {
        return domainManager.getUser(uuid);
    }

    @Override
    public CompletableFuture<Void> save(User user) {
        return domainManager.save(user);
    }

    @Override
    public User getUser(Player player) {
        return getOnlineUser(player.getUniqueId());
    }
}
