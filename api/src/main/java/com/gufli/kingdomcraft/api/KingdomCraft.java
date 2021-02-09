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

package com.gufli.kingdomcraft.api;

import com.gufli.kingdomcraft.api.chat.ChatManager;
import com.gufli.kingdomcraft.api.domain.*;
import com.gufli.kingdomcraft.api.editor.Editor;
import com.gufli.kingdomcraft.api.entity.PlatformPlayer;
import com.gufli.kingdomcraft.api.event.EventManager;
import com.gufli.kingdomcraft.api.language.Messages;
import com.gufli.kingdomcraft.api.placeholders.PlaceholderManager;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface KingdomCraft {

    // managers

    Messages getMessages();

    EventManager getEventManager();

    ChatManager getChatManager();

    PlaceholderManager getPlaceholderManager();

    Editor getEditor();

    // players

    Set<PlatformPlayer> getOnlinePlayers();

    PlatformPlayer getPlayer(UUID uuid);

    PlatformPlayer getPlayer(User user);

    PlatformPlayer getPlayer(String name);

    User getUser(PlatformPlayer player);

    // kingdoms

    Set<Kingdom> getKingdoms();

    Set<Kingdom> getKingdoms(boolean includeTemplate);

    Kingdom getKingdom(String name);

    Kingdom getKingdom(long id);

    Kingdom createKingdom(String name);

    Kingdom getTemplateKingdom();

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

    //

    CompletableFuture<Void> saveAsync(Model... models);

    <T extends Model> CompletableFuture<Void> saveAsync(Collection<T> models);

    CompletableFuture<Void> deleteAsync(Model... models);

    <T extends Model> CompletableFuture<Void> deleteAsync(Collection<T> models);

}
