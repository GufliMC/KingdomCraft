package com.guflan.kingdomcraft.api;

import com.guflan.kingdomcraft.api.chat.ChatManager;
import com.guflan.kingdomcraft.api.command.CommandManager;
import com.guflan.kingdomcraft.api.domain.models.*;
import com.guflan.kingdomcraft.api.entity.Player;
import com.guflan.kingdomcraft.api.event.EventManager;
import com.guflan.kingdomcraft.api.messages.MessageManager;
import com.guflan.kingdomcraft.api.placeholders.PlaceholderManager;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class KingdomCraft {

    public static KingdomCraftHandler INSTANCE;

    public final KingdomCraftPlugin getPlugin() {
        return INSTANCE.getPlugin();
    }

    // managers

    public static MessageManager getMessageManager() {
        return INSTANCE.getMessageManager();
    }

    public static CommandManager getCommandManager() {
        return INSTANCE.getCommandManager();
    }

    public static EventManager getEventManager() {
        return INSTANCE.getEventManager();
    }

    public static ChatManager getChatManager() {
        return INSTANCE.getChatManager();
    }

    public static PlaceholderManager getPlaceholderManager() {
        return INSTANCE.getPlaceholderManager();
    }

    // players

    public static List<Player> getOnlinePlayers() {
        return INSTANCE.getOnlinePlayers();
    }

    public static Player getPlayer(UUID uuid) {
        return INSTANCE.getPlayer(uuid);
    }

    public static Player getPlayer(User user) {
        return INSTANCE.getPlayer(user);
    }

    public static User getUser(Player player) {
        return INSTANCE.getUser(player);
    }

    // kingdoms

    public static List<Kingdom> getKingdoms() {
        return INSTANCE.getKingdoms();
    }

    public static Kingdom getKingdom(String name) {
        return INSTANCE.getKingdom(name);
    }

    public static Kingdom createKingdom(String name) {
        return INSTANCE.createKingdom(name);
    }

    public static CompletableFuture<Void> delete(Kingdom kingdom) {
        return INSTANCE.delete(kingdom);
    }

    public static CompletableFuture<Void> save(Kingdom kingdom) {
        return INSTANCE.save(kingdom);
    }

    // ranks

    public static CompletableFuture<Void> delete(Rank rank) {
        return INSTANCE.delete(rank);
    }

    public static CompletableFuture<Void> save(Rank rank) {
        return INSTANCE.save(rank);
    }

    // relations

    public static List<Relation> getRelations(Kingdom kingdom) {
        return INSTANCE.getRelations(kingdom);
    }

    public static void setRelation(Kingdom kingdom, Kingdom other, RelationType type) {
        INSTANCE.setRelation(kingdom, other, type);
    }

    public static Relation getRelation(Kingdom kingdom, Kingdom other) {
        return INSTANCE.getRelation(kingdom, other);
    }

    public static void addRelationRequest(Kingdom kingdom, Kingdom other, RelationType type) {
        INSTANCE.addRelationRequest(kingdom, other, type);
    }

    public static Relation getRelationRequest(Kingdom kingdom, Kingdom other) {
        return INSTANCE.getRelationRequest(kingdom, other);
    }

    public static void removeRelationRequest(Kingdom kingdom, Kingdom other) {
        INSTANCE.removeRelationRequest(kingdom, other);
    }

    // users

    public static List<User> getOnlineUsers() {
        return INSTANCE.getOnlineUsers();
    }

    public static User getOnlineUser(String name) {
        return INSTANCE.getOnlineUser(name);
    }

    public static User getOnlineUser(UUID uuid) {
        return INSTANCE.getOnlineUser(uuid);
    }

    public static CompletableFuture<List<User>> getUsers() {
        return INSTANCE.getUsers();
    }

    public static CompletableFuture<User> getUser(String name) {
        return INSTANCE.getUser(name);
    }

    public static CompletableFuture<User> getUser(UUID uuid) {
        return INSTANCE.getUser(uuid);
    }

    public static CompletableFuture<Void> save(User user){
        return INSTANCE.save(user);
    }

}
