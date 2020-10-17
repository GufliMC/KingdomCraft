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

package com.guflan.kingdomcraft.common.ebean;

import com.guflan.kingdomcraft.api.domain.*;
import com.guflan.kingdomcraft.common.KingdomCraftPlugin;
import com.guflan.kingdomcraft.common.ebean.beans.BKingdom;
import com.guflan.kingdomcraft.common.ebean.beans.BRank;
import com.guflan.kingdomcraft.common.ebean.beans.BRelation;
import com.guflan.kingdomcraft.common.ebean.beans.BUser;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class EBeanContext {

    private final List<BKingdom> kingdoms = new ArrayList<>();
    private final List<BRelation> relations = new ArrayList<>();

    private final List<BUser> users = new ArrayList<>();

    private final EBeanStorage storage;

    public EBeanContext(KingdomCraftPlugin plugin) {
        this.storage = new EBeanStorage(plugin);
    }

    public boolean init(String url, String driver, String username, String password) {
        if ( !this.storage.init(url, driver, username, password) ) {
            return false;
        }

        storage.getKingdoms().thenAccept(ks -> ks.forEach(k -> kingdoms.add((BKingdom) k)));
        storage.getRelations().thenAccept(rs -> rs.forEach(r -> relations.add((BRelation) r)));
        return true;
    }

    public List<Kingdom> getCachedKingdoms() {
        return new ArrayList<>(kingdoms);
    }

    public Kingdom getCachedKingdom(String name) {
        return kingdoms.stream().filter(k -> k.getName().equals(name)).findFirst().orElse(null);
    }

    public CompletableFuture<Void> delete(Kingdom kingdom) {
        kingdoms.remove(kingdom);
        return storage.delete(kingdom);
    }

    public CompletableFuture<Void> save(Kingdom kingdom) {
        return storage.save(kingdom);
    }

    public Kingdom createKingdom(String name) {
        BKingdom kingdom = new BKingdom();
        kingdom.name = name;

        kingdoms.add(kingdom);
        return kingdom;
    }

    // ranks

    public CompletableFuture<Void> delete(Rank rank) {
        return storage.delete(rank);
    }

    public CompletableFuture<Void> save(Rank rank) {
        return storage.save(rank);
    }

    // relations

    public List<Relation> getRelations(Kingdom kingdom) {
        return relations.stream().filter(r -> r.kingdom.equals(kingdom) || r.otherKingdom.equals(kingdom)).collect(Collectors.toList());
    }

    public Relation getRelation(Kingdom kingdom, Kingdom other) {
        return relations.stream().filter(r -> (r.kingdom.equals(kingdom) && r.otherKingdom.equals(other))
                || (r.kingdom.equals(other) && r.otherKingdom.equals(kingdom))).filter(r -> !r.isRequest()).findFirst().orElse(null);
    }

    private BRelation createRelation(Kingdom kingdom, Kingdom other, RelationType type, boolean isRequest) {
        BRelation rel = new BRelation();
        rel.kingdom = (BKingdom) kingdom;
        rel.otherKingdom = (BKingdom) other;
        rel.relation = type.getId();
        rel.isRequest = isRequest;
        return rel;
    }

    public void setRelation(Kingdom kingdom, Kingdom other, RelationType type) {
        Relation rel = getRelation(kingdom, other);
        if ( rel != null ) {
            storage.delete(rel);
            relations.remove(rel);
            return;
        }

        BRelation brel = createRelation(kingdom, other, type, false);
        storage.save(brel);
        relations.add(brel);
    }

    public void addRelationRequest(Kingdom kingdom, Kingdom other, RelationType type) {
        BRelation brel = createRelation(kingdom, other, type, true);
        storage.save(brel);
        relations.add(brel);
    }

    public Relation getRelationRequest(Kingdom kingdom, Kingdom other) {
        return relations.stream().filter(r -> r.kingdom.equals(kingdom) && r.otherKingdom.equals(other))
                .filter(Relation::isRequest).findFirst().orElse(null);
    }

    public void removeRelationRequest(Kingdom kingdom, Kingdom other) {
        Relation rel = getRelationRequest(kingdom, other);
        if ( rel == null ) {
            return;
        }

        storage.delete(rel);
        relations.remove(rel);
    }

    private void fit(Relation relation) {
        BRelation brel = (BRelation) relation;
        brel.kingdom = (BKingdom) getCachedKingdom(brel.kingdom.getName());
        brel.otherKingdom = (BKingdom) getCachedKingdom(brel.otherKingdom.getName());
    }

    // users

    public List<User> getCachedUsers() {
        return new ArrayList<>(users);
    }

    public User getCachedUser(String name) {
        return users.stream().filter(u -> u.name.equals(name)).findFirst().orElse(null);
    }

    public User getCachedUser(UUID uuid) {
        return users.stream().filter(u -> u.id.equals(uuid.toString())).findFirst().orElse(null);
    }

    public CompletableFuture<List<User>> getUsers() {
        return storage.getUsers().thenApply(users -> {
            users.forEach(this::fit);
            return users;
        });
    }

    public CompletableFuture<User> getUser(String name) {
        User user = getCachedUser(name);
        if ( user != null ) {
            return CompletableFuture.completedFuture(user);
        }

        return storage.getUser(name).thenApply(u -> {
            fit(u);
            return u;
        });
    }

    public CompletableFuture<User> getUser(UUID uuid) {
        User user = getCachedUser(uuid);
        if ( user != null ) {
            return CompletableFuture.completedFuture(user);
        }

        return storage.getUser(uuid).thenApply(u -> {
            fit(u);
            return u;
        });
    }

    public CompletableFuture<Void> delete(User user) {
        return storage.delete(user);
    }

    public CompletableFuture<Void> save(User user) {
        return storage.save(user);
    }

    public User createUser(UUID uuid, String name) {
        BUser user = new BUser();
        user.id = uuid.toString();
        user.name = name;
        return user;
    }

    public void addCachedUser(User user) {
        if ( !users.contains(user) ) {
            users.add((BUser) user);
        }
    }

    public void removeCachedUser(User user) {
        users.remove(user);
    }

    private void fit(User user) {
        BUser buser = (BUser) user;
        if ( buser.kingdom != null ) {
            buser.kingdom = (BKingdom) getCachedKingdom(buser.kingdom.getName());

            if ( buser.rank != null ){
                buser.rank = (BRank) buser.kingdom.getRank(buser.rank.getName());
            }
        }
    }

}
