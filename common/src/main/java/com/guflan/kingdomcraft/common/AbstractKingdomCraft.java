/*
 * This file is part of KingdomCraft.
 *
 * KingdomCraft is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * KingdomCraft is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with KingdomCraft. If not, see <https://www.gnu.org/licenses/>.
 */

package com.guflan.kingdomcraft.common;

import com.guflan.kingdomcraft.api.KingdomCraft;
import com.guflan.kingdomcraft.api.KingdomCraftProvider;
import com.guflan.kingdomcraft.api.chat.ChatManager;
import com.guflan.kingdomcraft.api.command.CommandManager;
import com.guflan.kingdomcraft.api.domain.*;
import com.guflan.kingdomcraft.api.entity.PlatformPlayer;
import com.guflan.kingdomcraft.api.event.EventManager;
import com.guflan.kingdomcraft.api.placeholders.PlaceholderManager;
import com.guflan.kingdomcraft.common.chat.ChatDispatcher;
import com.guflan.kingdomcraft.common.chat.ChatManagerImpl;
import com.guflan.kingdomcraft.common.command.CommandDispatcher;
import com.guflan.kingdomcraft.common.command.CommandManagerImpl;
import com.guflan.kingdomcraft.common.config.KingdomCraftConfig;
import com.guflan.kingdomcraft.common.ebean.EBeanContext;
import com.guflan.kingdomcraft.common.event.EventDispatcher;
import com.guflan.kingdomcraft.common.event.EventManagerImpl;
import com.guflan.kingdomcraft.common.placeholders.PlaceholderManagerImpl;
import com.guflan.kingdomcraft.common.util.Teleporter;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public abstract class AbstractKingdomCraft implements KingdomCraft {

    private final KingdomCraftPlugin plugin;
    private final KingdomCraftConfig config;
    private final EBeanContext context;

    //

    private final CommandManagerImpl commandManager;
    private final CommandDispatcher commandDispatcher;

    private final EventManagerImpl eventManager;
    private final EventDispatcher eventDispatcher;

    private final ChatManagerImpl chatManager;
    private final ChatDispatcher chatDispatcher;

    private final PlaceholderManagerImpl placeholderManager;


    public AbstractKingdomCraft(KingdomCraftPlugin plugin, KingdomCraftConfig config, EBeanContext context) {
        KingdomCraftProvider.register(this);
        Teleporter.register(this);

        this.plugin = plugin;
        this.config = config;
        this.context = context;

        this.commandManager = new CommandManagerImpl(this);
        this.commandDispatcher = new CommandDispatcher(commandManager);

        this.eventManager = new EventManagerImpl();
        this.eventDispatcher = new EventDispatcher(this.eventManager);

        this.chatManager = new ChatManagerImpl(this);
        this.chatDispatcher = new ChatDispatcher(this, chatManager);

        this.placeholderManager = new PlaceholderManagerImpl(this);
    }

    //

    public KingdomCraftPlugin getPlugin() {
        return plugin;
    }

    public KingdomCraftConfig getConfig() {
        return config;
    }

    @Override
    public PlaceholderManager getPlaceholderManager() {
        return placeholderManager;
    }

    @Override
    public ChatManager getChatManager() {
        return chatManager;
    }

    public ChatDispatcher getChatDispatcher() {
        return chatDispatcher;
    }

    @Override
    public EventManager getEventManager() {
        return eventManager;
    }

    public EventDispatcher getEventDispatcher() {
        return this.eventDispatcher;
    }

    @Override
    public CommandManager getCommandManager() {
        return commandManager;
    }

    public CommandDispatcher getCommandDispatcher() {
        return commandDispatcher;
    }

    //

    public void join(PlatformPlayer player) {
        plugin.getScheduler().async().execute(() -> {
            try {
                User user = getUser(player.getUniqueId()).get();
                if ( user == null ) {
                    user = getUser(player.getName()).get();
                }

                if ( user == null ) {
                    user = context.createUser(player.getUniqueId(), player.getName());
                }

                context.addCachedUser(user);
                eventDispatcher.dispatchJoin(player);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
    }

    public void quit(PlatformPlayer player) {
        User user = context.getCachedUser(player.getUniqueId());
        context.removeCachedUser(user);
        eventDispatcher.dispatchQuit(player);
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
        eventDispatcher.dispatchKingdomCreate(kingdom);
        return kingdom;
    }

    @Override
    public CompletableFuture<Void> delete(Kingdom kingdom) {
        eventDispatcher.dispatchKingdomDelete(kingdom);
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

    // attributes

    @Override
    public CompletableFuture<Void> delete(KingdomAttribute property) {
        return context.delete(property);
    }

    @Override
    public CompletableFuture<Void> save(KingdomAttribute property) {
        return context.save(property);
    }

    // attributes

    @Override
    public CompletableFuture<Void> delete(RankAttribute property) {
        return context.delete(property);
    }

    @Override
    public CompletableFuture<Void> save(RankAttribute property) {
        return context.save(property);
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
    public User getUser(PlatformPlayer player) {
        return getOnlineUser(player.getUniqueId());
    }
}
