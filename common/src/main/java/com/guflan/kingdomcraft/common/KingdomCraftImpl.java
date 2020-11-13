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
import com.guflan.kingdomcraft.api.domain.*;
import com.guflan.kingdomcraft.api.editor.Editor;
import com.guflan.kingdomcraft.api.entity.PlatformPlayer;
import com.guflan.kingdomcraft.api.event.EventManager;
import com.guflan.kingdomcraft.api.placeholders.PlaceholderManager;
import com.guflan.kingdomcraft.common.chat.ChatDispatcher;
import com.guflan.kingdomcraft.common.chat.ChatManagerImpl;
import com.guflan.kingdomcraft.common.command.CommandDispatcher;
import com.guflan.kingdomcraft.common.command.CommandManager;
import com.guflan.kingdomcraft.common.config.Configuration;
import com.guflan.kingdomcraft.common.config.KingdomCraftConfig;
import com.guflan.kingdomcraft.common.ebean.StorageContext;
import com.guflan.kingdomcraft.common.editor.EditorImpl;
import com.guflan.kingdomcraft.common.event.EventDispatcher;
import com.guflan.kingdomcraft.common.event.EventManagerImpl;
import com.guflan.kingdomcraft.common.messages.MessageManager;
import com.guflan.kingdomcraft.common.permissions.PermissionManager;
import com.guflan.kingdomcraft.common.placeholders.PlaceholderManagerImpl;
import com.guflan.kingdomcraft.common.util.Teleporter;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutionException;

public class KingdomCraftImpl implements KingdomCraft {

    private final KingdomCraftPlugin plugin;
    private final KingdomCraftConfig config;
    private final StorageContext context;

    private final CommandManager commandManager;
    private final CommandDispatcher commandDispatcher;

    private final MessageManager messageManager;
    private final PermissionManager permissionManager;

    private final EventManagerImpl eventManager;
    private final EventDispatcher eventDispatcher;

    private final ChatManagerImpl chatManager;
    private final ChatDispatcher chatDispatcher;

    private final PlaceholderManagerImpl placeholderManager;
    private final EditorImpl editor;

    //

    private final Set<PlatformPlayer> onlinePlayers = new CopyOnWriteArraySet<>();

    public KingdomCraftImpl(KingdomCraftPlugin plugin,
                            StorageContext context,
                            Configuration config,
                            Configuration chatConfig,
                            Configuration groupsConfig) {

        this.plugin = plugin;
        this.context = context;

        this.config = new KingdomCraftConfig(config);

        this.messageManager = new MessageManager(this);

        this.commandManager = new CommandManager(this);
        this.commandDispatcher = new CommandDispatcher(commandManager);

        this.eventManager = new EventManagerImpl();
        this.eventDispatcher = new EventDispatcher(this.eventManager);

        this.chatManager = new ChatManagerImpl(this, chatConfig);
        this.chatDispatcher = new ChatDispatcher(this, chatManager);

        this.placeholderManager = new PlaceholderManagerImpl(this);
        this.permissionManager = new PermissionManager(this, groupsConfig);

        this.editor = new EditorImpl(this);

        KingdomCraftProvider.register(this);
        Teleporter.register(this);
    }

    //

    public KingdomCraftPlugin getPlugin() {
        return plugin;
    }

    public KingdomCraftConfig getConfig() {
        return config;
    }

    // messages

    public MessageManager getMessageManager() {
        return messageManager;
    }

    // placeholders

    @Override
    public PlaceholderManager getPlaceholderManager() {
        return placeholderManager;
    }

    // permissions

    public PermissionManager getPermissionManager() {
        return permissionManager;
    }

    // chat

    @Override
    public ChatManager getChatManager() {
        return chatManager;
    }

    public ChatDispatcher getChatDispatcher() {
        return chatDispatcher;
    }

    public ChatManagerImpl getChatManagerImpl() {
        return chatManager;
    }

    // events

    @Override
    public EventManager getEventManager() {
        return eventManager;
    }

    public EventDispatcher getEventDispatcher() {
        return this.eventDispatcher;
    }

    // commands

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public CommandDispatcher getCommandDispatcher() {
        return commandDispatcher;
    }

    // editor

    @Override
    public Editor getEditor() {
        return editor;
    }

    public EditorImpl getEditorImpl() {
        return editor;
    }

    // players

    @Override
    public Set<PlatformPlayer> getOnlinePlayers() {
        return onlinePlayers;
    }

    @Override
    public PlatformPlayer getPlayer(UUID uuid) {
        return onlinePlayers.stream().filter(p -> p.getUniqueId().equals(uuid)).findFirst().orElse(null);
    }

    @Override
    public PlatformPlayer getPlayer(User user) {
        return getPlayer(user.getUniqueId());
    }

    @Override
    public PlatformPlayer getPlayer(String name) {
        return onlinePlayers.stream().filter(p -> p.getName().equalsIgnoreCase(name)).findFirst().orElse(null);

    }

    // kingdoms

    @Override
    public Set<Kingdom> getKingdoms() {
        return context.getKingdoms();
    }

    @Override
    public Kingdom getKingdom(String name) {
        return context.getKingdom(name);
    }

    @Override
    public Kingdom createKingdom(String name) {
        if ( getKingdom(name) != null ) {
            throw new IllegalArgumentException("A kingdom with that name already exists.");
        }
        Kingdom kingdom = context.createKingdom(name);
        eventDispatcher.dispatchKingdomCreate(kingdom);
        return kingdom;
    }

    // relations

    @Override
    public Set<Relation> getRelations(Kingdom kingdom) {
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
    public Set<User> getOnlineUsers() {
        return context.getOnlineUsers();
    }

    @Override
    public User getOnlineUser(String name) {
        return context.getOnlineUser(name);
    }

    @Override
    public User getOnlineUser(UUID uuid) {
        return context.getOnlineUser(uuid);
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
    public User getUser(PlatformPlayer player) {
        return getOnlineUser(player.getUniqueId());
    }

    //

    @Override
    public CompletableFuture<Void> saveAsync(Model... models) {
        return context.saveAsync(Arrays.asList(models));
    }

    @Override
    public CompletableFuture<Void> saveAsync(Collection<Model> models) {
        return context.saveAsync(models);
    }

    @Override
    public CompletableFuture<Void> deleteAsync(Model... models) {
        return context.deleteAsync(Arrays.asList(models));
    }

    //

    public void onLoad(PlatformPlayer player) {
        onlinePlayers.add(player);
        try {
            User user = getUser(player.getUniqueId()).get();
            if ( user == null ) {
                user = getUser(player.getName()).get();
                context.update(user, player);
            } else if ( !user.getName().equals(player.getName()) ) {
                context.update(user, player);
            }

            if ( user == null ) {
                user = context.createUser(player.getUniqueId(), player.getName());
                saveAsync(user);
            }

            context.addOnlineUser(user);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void onJoin(PlatformPlayer player) {
        onLoad(player);
        eventDispatcher.dispatchJoin(player);
    }

    public void onQuit(PlatformPlayer player) {
        eventDispatcher.dispatchQuit(player);
        onlinePlayers.remove(player);

        User user = context.getOnlineUser(player.getUniqueId());
        context.removeOnlineUser(user);
    }
}
