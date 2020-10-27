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
import com.guflan.kingdomcraft.api.messages.MessageManager;
import com.guflan.kingdomcraft.api.placeholders.PlaceholderManager;
import com.guflan.kingdomcraft.common.chat.ChatDispatcher;
import com.guflan.kingdomcraft.common.chat.ChatManagerImpl;
import com.guflan.kingdomcraft.common.command.CommandDispatcher;
import com.guflan.kingdomcraft.common.command.CommandManagerImpl;
import com.guflan.kingdomcraft.common.config.Configuration;
import com.guflan.kingdomcraft.common.config.KingdomCraftConfig;
import com.guflan.kingdomcraft.common.ebean.StorageContext;
import com.guflan.kingdomcraft.common.event.EventDispatcher;
import com.guflan.kingdomcraft.common.event.EventManagerImpl;
import com.guflan.kingdomcraft.common.permissions.PermissionManager;
import com.guflan.kingdomcraft.common.placeholders.PlaceholderManagerImpl;
import com.guflan.kingdomcraft.common.util.Teleporter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class KingdomCraftImpl implements KingdomCraft {

    private final KingdomCraftPlugin plugin;
    private final KingdomCraftConfig config;
    private final StorageContext context;

    //

    private final CommandManagerImpl commandManager;
    private final CommandDispatcher commandDispatcher;

    private final EventManagerImpl eventManager;
    private final EventDispatcher eventDispatcher;

    private final ChatManagerImpl chatManager;
    private final ChatDispatcher chatDispatcher;

    private final PlaceholderManagerImpl placeholderManager;
    private final MessageManager messageManager;
    private final PermissionManager permissionManager;

    //

    private final List<PlatformPlayer> onlinePlayers = new ArrayList<>();

    public KingdomCraftImpl(KingdomCraftPlugin plugin,
                            StorageContext context,
                            MessageManager messageManager,
                            Configuration config,
                            Configuration chatConfig,
                            Configuration groupsConfig) {

        this.plugin = plugin;
        this.context = context;
        this.messageManager = messageManager;

        this.config = new KingdomCraftConfig(config);

        this.commandManager = new CommandManagerImpl(this);
        this.commandDispatcher = new CommandDispatcher(commandManager);

        this.eventManager = new EventManagerImpl();
        this.eventDispatcher = new EventDispatcher(this.eventManager);

        this.chatManager = new ChatManagerImpl(this, chatConfig);
        this.chatDispatcher = new ChatDispatcher(this, chatManager);

        this.placeholderManager = new PlaceholderManagerImpl(this);
        this.permissionManager = new PermissionManager(this, groupsConfig);

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

    @Override
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

    // events

    @Override
    public EventManager getEventManager() {
        return eventManager;
    }

    public EventDispatcher getEventDispatcher() {
        return this.eventDispatcher;
    }

    // commands

    @Override
    public CommandManager getCommandManager() {
        return commandManager;
    }

    public CommandDispatcher getCommandDispatcher() {
        return commandDispatcher;
    }

    // players

    @Override
    public List<PlatformPlayer> getOnlinePlayers() {
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

    // kingdoms

    @Override
    public List<Kingdom> getKingdoms() {
        return context.getKingdoms();
    }

    @Override
    public Kingdom getKingdom(String name) {
        return context.getKingdom(name);
    }

    @Override
    public Kingdom createKingdom(String name) {
        Kingdom kingdom = context.createKingdom(name);
        eventDispatcher.dispatchKingdomCreate(kingdom);
        return kingdom;
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

    public void onJoin(PlatformPlayer player) {
        onlinePlayers.add(player);
        plugin.getScheduler().executeAsync(() -> {
            try {
                User user = getUser(player.getUniqueId()).get();
                if ( user == null ) {
                    user = getUser(player.getName()).get();
                }

                if ( user == null ) {
                    user = context.createUser(player.getUniqueId(), player.getName());
                    plugin.getScheduler().executeAsync(user::save);
                }

                context.addOnlineUser(user);
                eventDispatcher.dispatchJoin(player);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
    }

    public void onQuit(PlatformPlayer player) {
        eventDispatcher.dispatchQuit(player);

        User user = context.getOnlineUser(player.getUniqueId());
        context.removeOnlineUser(user);
        onlinePlayers.remove(player);
    }
}
