package com.guflan.kingdomcraft.common;

import com.guflan.kingdomcraft.api.KingdomCraft;
import com.guflan.kingdomcraft.api.chat.ChatManager;
import com.guflan.kingdomcraft.api.command.CommandManager;
import com.guflan.kingdomcraft.api.domain.Kingdom;
import com.guflan.kingdomcraft.api.domain.Rank;
import com.guflan.kingdomcraft.api.domain.User;
import com.guflan.kingdomcraft.api.entity.Player;
import com.guflan.kingdomcraft.api.event.EventManager;
import com.guflan.kingdomcraft.api.placeholders.PlaceholderManager;
import com.guflan.kingdomcraft.api.storage.Storage;
import com.guflan.kingdomcraft.common.chat.BasicChatManager;
import com.guflan.kingdomcraft.common.command.DefaultCommandManager;
import com.guflan.kingdomcraft.common.event.DefaultEventManager;
import com.guflan.kingdomcraft.common.placeholders.DefaultPlaceholderManager;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public abstract class AbstractKingdomCraft implements KingdomCraft {

    private final Storage storage;

    //

    private final DefaultCommandManager commandManager;
    private final DefaultEventManager eventManager;
    private final BasicChatManager chatManager;
    private final DefaultPlaceholderManager placeholderManager;

    //

    private final Set<Kingdom> kingdoms = new HashSet<>();
    private final Set<User> onlineUsers = new HashSet<>();

    public AbstractKingdomCraft(Storage storage) {
        this.storage = storage;

        try {
            kingdoms.addAll(storage.getKingdoms().get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

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
                if (user == null) {
                    user = getUser(player.getName()).get();
                }

                if (user == null) {
                    user = storage.createUser(player.getUniqueId(), player.getName());
                }

                onlineUsers.add(user);
                // TODO join event
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void quit(Player player) {
        onlineUsers.removeIf(u -> u.getUniqueId().equals(player.getUniqueId()));
    }

    // kingdoms

    @Override
    public Set<Kingdom> getKingdoms() {
        return kingdoms;
    }

    @Override
    public Kingdom getKingdom(String name) {
        return kingdoms.stream().filter(k -> k.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    @Override
    public Kingdom createKingdom(String name) {
        Kingdom kingdom = storage.createKingdom(name);
        save(kingdom);
        kingdoms.add(kingdom);
        eventManager.kingdomCreate(kingdom);
        return kingdom;
    }

    @Override
    public CompletableFuture<Void> delete(Kingdom kingdom) {
        return storage.delete(kingdom);
    }

    @Override
    public CompletableFuture<Void> save(Kingdom kingdom) {
        return storage.save(kingdom);
    }

    @Override
    public CompletableFuture<Void> delete(Rank rank) {
        return storage.delete(rank);
    }

    @Override
    public CompletableFuture<Void> save(Rank rank) {
        return storage.save(rank);
    }

    // users

    @Override
    public Set<User> getOnlineUsers() {
        return onlineUsers;
    }

    @Override
    public User getOnlineUser(String name) {
        return onlineUsers.stream().filter(u -> u.getName().equals(name)).findFirst().orElse(null);
    }

    @Override
    public User getOnlineUser(UUID uuid) {
        return onlineUsers.stream().filter(u -> u.getUniqueId().equals(uuid)).findFirst().orElse(null);
    }

    @Override
    public CompletableFuture<Set<User>> getUsers() {
        return storage.getUsers();
    }

    @Override
    public CompletableFuture<User> getUser(String name) {
        User user = getOnlineUser(name);
        if ( user != null ) {
            return CompletableFuture.completedFuture(user);
        }

        return storage.getUser(name);
    }

    @Override
    public CompletableFuture<User> getUser(UUID uuid) {
        User user = getOnlineUser(uuid);
        if ( user != null ) {
            return CompletableFuture.completedFuture(user);
        }

        return storage.getUser(uuid);
    }

    @Override
    public CompletableFuture<Void> save(User user) {
        return storage.save(user);
    }

    @Override
    public User getUser(Player player) {
        return getOnlineUser(player.getUniqueId());
    }
}
