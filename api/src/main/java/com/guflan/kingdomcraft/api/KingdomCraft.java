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
import com.guflan.kingdomcraft.api.domain.Kingdom;
import com.guflan.kingdomcraft.api.domain.Relation;
import com.guflan.kingdomcraft.api.domain.RelationType;
import com.guflan.kingdomcraft.api.domain.User;
import com.guflan.kingdomcraft.api.entity.PlatformPlayer;
import com.guflan.kingdomcraft.api.event.EventManager;
import com.guflan.kingdomcraft.api.placeholders.PlaceholderManager;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface KingdomCraft {

    // managers

    EventManager getEventManager();

    ChatManager getChatManager();

    PlaceholderManager getPlaceholderManager();

    // players

    Set<PlatformPlayer> getOnlinePlayers();

    PlatformPlayer getPlayer(UUID uuid);

    PlatformPlayer getPlayer(User user);

    User getUser(PlatformPlayer player);

    // kingdoms

    Set<Kingdom> getKingdoms();

    Kingdom getKingdom(String name);

    Kingdom createKingdom(String name);

    // relations

    Set<Relation> getRelations(Kingdom kingdom);

    void setRelation(Kingdom kingdom, Kingdom other, RelationType type);

    Relation getRelation(Kingdom kingdom, Kingdom other);

    void addRelationRequest(Kingdom kingdom, Kingdom other, RelationType type);

    Relation getRelationRequest(Kingdom kingdom, Kingdom other);

    void removeRelationRequest(Kingdom kingdom, Kingdom other);

    // users

    Set<User> getOnlineUsers();

    User getOnlineUser(String name);

    User getOnlineUser(UUID uuid);

    CompletableFuture<List<User>> getUsers();

    CompletableFuture<User> getUser(String name);

    CompletableFuture<User> getUser(UUID uuid);

}
