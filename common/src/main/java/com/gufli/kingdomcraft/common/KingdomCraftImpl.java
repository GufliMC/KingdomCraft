/*
 * This file is part of KingdomCraft.
 *
 * KingdomCraft is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * KingdomCraft is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with KingdomCraft. If not, see <https://www.gnu.org/licenses/>.
 */

package com.gufli.kingdomcraft.common;

import com.gufli.kingdomcraft.api.KingdomCraft;
import com.gufli.kingdomcraft.api.KingdomCraftProvider;
import com.gufli.kingdomcraft.api.chat.ChatManager;
import com.gufli.kingdomcraft.api.domain.*;
import com.gufli.kingdomcraft.api.editor.Editor;
import com.gufli.kingdomcraft.api.entity.PlatformPlayer;
import com.gufli.kingdomcraft.api.event.EventManager;
import com.gufli.kingdomcraft.api.placeholders.PlaceholderManager;
import com.gufli.kingdomcraft.common.chat.ChatDispatcher;
import com.gufli.kingdomcraft.common.chat.ChatManagerImpl;
import com.gufli.kingdomcraft.common.command.CommandDispatcher;
import com.gufli.kingdomcraft.common.command.CommandManager;
import com.gufli.kingdomcraft.common.config.Configuration;
import com.gufli.kingdomcraft.common.config.KingdomCraftConfig;
import com.gufli.kingdomcraft.common.ebean.StorageContext;
import com.gufli.kingdomcraft.common.editor.EditorImpl;
import com.gufli.kingdomcraft.common.event.EventDispatcher;
import com.gufli.kingdomcraft.common.event.EventManagerImpl;
import com.gufli.kingdomcraft.common.messages.MessagesImpl;
import com.gufli.kingdomcraft.common.permissions.PermissionManager;
import com.gufli.kingdomcraft.common.placeholders.PlaceholderManagerImpl;
import com.gufli.kingdomcraft.common.util.Teleporter;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class KingdomCraftImpl implements KingdomCraft {

    private final KingdomCraftPlugin plugin;
    private final KingdomCraftConfig config = new KingdomCraftConfig();
    private final StorageContext context;

    private final CommandManager commandManager;
    private final CommandDispatcher commandDispatcher;

    private final MessagesImpl messages;
    private final PermissionManager permissionManager;

    private final EventManagerImpl eventManager;
    private final EventDispatcher eventDispatcher;

    private final ChatManagerImpl chatManager;
    private final ChatDispatcher chatDispatcher;

    private final PlaceholderManagerImpl placeholderManager;
    private final EditorImpl editor;

    //

    public KingdomCraftImpl(KingdomCraftPlugin plugin,
                            StorageContext context) {

        this.plugin = plugin;
        this.context = context;

        this.messages = new MessagesImpl(plugin);

        this.commandManager = new CommandManager(this);
        this.commandDispatcher = new CommandDispatcher(commandManager);
        context.registerDumpCommand(this);

        this.eventManager = new EventManagerImpl();
        this.eventDispatcher = new EventDispatcher(this.eventManager);

        this.chatManager = new ChatManagerImpl(this);
        this.chatDispatcher = new ChatDispatcher(this, chatManager);

        this.placeholderManager = new PlaceholderManagerImpl(this);
        this.permissionManager = new PermissionManager(this);

        this.editor = new EditorImpl(this);

        KingdomCraftProvider.register(this);
        Teleporter.register(this);
    }

    public void stop() {
        context.stop();
    }

    //

    public KingdomCraftPlugin getPlugin() {
        return plugin;
    }

    public KingdomCraftConfig getConfig() {
        return config;
    }

    // messages

    @Override
    public MessagesImpl getMessages() {
        return messages;
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
    public ChatManagerImpl getChatManager() {
        return chatManager;
    }

    public ChatDispatcher getChatDispatcher() {
        return chatDispatcher;
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
    public EditorImpl getEditor() {
        return editor;
    }

    // players

    @Override
    public Set<PlatformPlayer> getOnlinePlayers() {
        return context.getPlayers();
    }

    @Override
    public PlatformPlayer getPlayer(UUID uuid) {
        return context.getPlayer(uuid);
    }

    @Override
    public PlatformPlayer getPlayer(User user) {
        return context.getPlayer(user);
    }

    @Override
    public PlatformPlayer getPlayer(String name) {
        return context.getPlayer(name);

    }

    // kingdoms

    @Override
    public Set<Kingdom> getKingdoms() {
        return context.getKingdoms();
    }

    @Override
    public Set<Kingdom> getKingdoms(boolean includeTemplate) {
        Set<Kingdom> kingdoms = context.getKingdoms();
        if ( includeTemplate ) {
            kingdoms.add(getTemplateKingdom());
        }
        return kingdoms;
    }

    @Override
    public Kingdom getKingdom(String name) {
        if ( name.equalsIgnoreCase("template") ) {
            return getTemplateKingdom();
        }
        return context.getKingdom(name);
    }

    @Override
    public Kingdom getKingdom(long id) {
        if ( id == -1 ) {
            return getTemplateKingdom();
        }
        return context.getKingdom(id);
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

    @Override
    public Kingdom getTemplateKingdom() {
        return context.getTemplateKingdom();
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

    public void clearUsers() {
        context.clearUsers();
    }

    public void purgeUsers() {
        context.purgeUsers();
    }

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
        return context.saveAsync(models);
    }

    @Override
    public <T extends Model> CompletableFuture<Void> saveAsync(Collection<T> models) {
        return context.saveAsync(models);
    }

    @Override
    public CompletableFuture<Void> deleteAsync(Model... models) {
        return context.deleteAsync(models);
    }

    @Override
    public <T extends Model> CompletableFuture<Void> deleteAsync(Collection<T> models) {
        return context.deleteAsync(models);
    }

    //

    public boolean onLoad(PlatformPlayer player) {
        try {
            User user = getUser(player.getUniqueId()).get();
            if ( user == null ) {
                user = getUser(player.getName()).get();
            }

            if ( user == null ) {
                user = context.createUser(player.getUniqueId(), player.getName());
                saveAsync(user);
            } else if ( !user.getName().equals(player.getName()) ) {
                context.updateName(user, player);
            }  else if ( !user.getUniqueId().equals(player.getUniqueId()) ) {
                context.updateUUID(user, player);
            }else {
                context.login(user);
            }

            context.addPlayer(player, user);
            return true;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean onLogin(PlatformPlayer player) {
        if ( onLoad(player) ) {
            eventDispatcher.dispatchLogin(player);
            return true;
        }
        return false;
    }

    public void onQuit(PlatformPlayer player) {
        eventDispatcher.dispatchQuit(player);
        context.removePlayer(player);
    }
}
