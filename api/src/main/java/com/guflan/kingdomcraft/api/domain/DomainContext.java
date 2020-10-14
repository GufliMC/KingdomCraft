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

package com.guflan.kingdomcraft.api.domain;

import com.guflan.kingdomcraft.api.domain.models.*;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface DomainContext {

    // kingdoms

    List<Kingdom> getCachedKingdoms();

    Kingdom getCachedKingdom(String name);

    CompletableFuture<Void> delete(Kingdom kingdom);

    CompletableFuture<Void> save(Kingdom kingdom);

    Kingdom createKingdom(String name);

    // ranks

    CompletableFuture<Void> delete(Rank rank);

    CompletableFuture<Void> save(Rank rank);

    // relations

    List<Relation> getRelations(Kingdom kingdom);

    void setRelation(Kingdom kingdom, Kingdom other, RelationType type);

    Relation getRelation(Kingdom kingdom, Kingdom other);

    void addRelationRequest(Kingdom kingdom, Kingdom other, RelationType type);

    Relation getRelationRequest(Kingdom kingdom, Kingdom other);

    void removeRelationRequest(Kingdom kingdom, Kingdom other);

    // users

    List<User> getCachedUsers();

    User getCachedUser(String name);

    User getCachedUser(UUID uuid);

    CompletableFuture<List<User>> getUsers();

    CompletableFuture<User> getUser(String name);

    CompletableFuture<User> getUser(UUID uuid);

    CompletableFuture<Void> delete(User user);

    CompletableFuture<Void> save(User user);

    User createUser(UUID uuid, String name);

    // cache

    void addCachedUser(User user);

    void removeCachedUser(User user);

}
