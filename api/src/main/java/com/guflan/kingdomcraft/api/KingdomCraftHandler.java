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

public abstract class KingdomCraftHandler {

    protected final KingdomCraftPlugin plugin;

    public KingdomCraftHandler(KingdomCraftPlugin plugin) {
        this.plugin = plugin;
        KingdomCraft.INSTANCE = this;
    }

    public final KingdomCraftPlugin getPlugin() {
        return plugin;
    }

    // managers

    public abstract MessageManager getMessageManager();

    public abstract CommandManager getCommandManager();

    public abstract EventManager getEventManager();

    public abstract ChatManager getChatManager();

    public abstract PlaceholderManager getPlaceholderManager();

    // players

    public abstract List<Player> getOnlinePlayers();

    public abstract Player getPlayer(UUID uuid);

    public abstract Player getPlayer(User user);

    public abstract User getUser(Player player);

    // kingdoms

    public abstract List<Kingdom> getKingdoms();

    public abstract Kingdom getKingdom(String name);

    public abstract Kingdom createKingdom(String name);

    public abstract CompletableFuture<Void> delete(Kingdom kingdom);

    public abstract CompletableFuture<Void> save(Kingdom kingdom);

    // ranks

    public abstract CompletableFuture<Void> delete(Rank rank);

    public abstract CompletableFuture<Void> save(Rank rank);

    // relations

    public abstract List<Relation> getRelations(Kingdom kingdom);

    public abstract void setRelation(Kingdom kingdom, Kingdom other, RelationType type);

    public abstract Relation getRelation(Kingdom kingdom, Kingdom other);

    public abstract void addRelationRequest(Kingdom kingdom, Kingdom other, RelationType type);

    public abstract Relation getRelationRequest(Kingdom kingdom, Kingdom other);

    public abstract void removeRelationRequest(Kingdom kingdom, Kingdom other);

    // users

    public abstract List<User> getOnlineUsers();

    public abstract User getOnlineUser(String name);

    public abstract User getOnlineUser(UUID uuid);

    public abstract CompletableFuture<List<User>> getUsers();

    public abstract CompletableFuture<User> getUser(String name);

    public abstract CompletableFuture<User> getUser(UUID uuid);

    public abstract CompletableFuture<Void> save(User user);

    //

    public abstract void join(Player player);

    public abstract void quit(Player player);

}
