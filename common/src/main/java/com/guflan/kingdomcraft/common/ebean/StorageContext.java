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
import com.guflan.kingdomcraft.common.ebean.beans.*;
import com.guflan.kingdomcraft.common.ebean.beans.query.QBKingdom;
import com.guflan.kingdomcraft.common.ebean.beans.query.QBRelation;
import com.guflan.kingdomcraft.common.ebean.beans.query.QBUser;
import io.ebean.DatabaseFactory;
import io.ebean.config.DatabaseConfig;
import io.ebean.datasource.DataSourceConfig;
import io.ebean.datasource.DataSourceFactory;
import io.ebean.datasource.DataSourcePool;
import io.ebean.migration.MigrationConfig;
import io.ebean.migration.MigrationException;
import io.ebean.migration.MigrationRunner;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class StorageContext {

    public static final List<BKingdom> kingdoms = new ArrayList<>();
    public static final List<BRelation> relations = new ArrayList<>();

    public static final List<BUser> users = new ArrayList<>();

    private final KingdomCraftPlugin plugin;

    public StorageContext(KingdomCraftPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean init(String url, String driver, String username, String password) {
        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        dataSourceConfig.setUrl(url);
        dataSourceConfig.setDriver(driver);
        dataSourceConfig.setUsername(username);
        dataSourceConfig.setPassword(password);

        DataSourcePool pool = DataSourceFactory.create("kingdomcraft", dataSourceConfig);

        // run migrations
        try {
            migrate(pool);
        } catch (MigrationException ex) {
            if ( ex.getCause() != null ) {
                plugin.log(ex.getCause().getMessage(), Level.SEVERE);
            } else {
                plugin.log(ex.getMessage(), Level.SEVERE);
            }
            return false;
        }

        // create database
        connect(pool);

        // load cache

        kingdoms.addAll(new QBKingdom().findList());

        new QBRelation().findList().forEach(rel ->  {
            reassign(rel);
            relations.add(rel);
        });

        return true;
    }

    private void migrate(DataSourcePool pool) {
        MigrationConfig config = new MigrationConfig();

        Connection conn = null;
        try {
            conn = pool.getConnection();
            String platform = conn.getMetaData().getDatabaseProductName().toLowerCase();
            config.setMigrationPath("dbmigration/" + platform);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        MigrationRunner runner = new MigrationRunner(config);
        runner.run(conn);
    }

    private void connect(DataSourcePool pool) {
        DatabaseConfig config = new DatabaseConfig();
        config.setDataSource(pool);
        config.setRegister(true);

        DatabaseFactory.create(config);
    }

    public List<Kingdom> getKingdoms() {
        return new ArrayList<>(kingdoms);
    }

    public Kingdom getKingdom(String name) {
        return kingdoms.stream().filter(k -> k.getName().equals(name)).findFirst().orElse(null);
    }

    public Kingdom createKingdom(String name) {
        BKingdom kingdom = new BKingdom();
        kingdom.name = name;
        kingdoms.add(kingdom);
        return kingdom;
    }

    // relations

    public List<Relation> getRelations(Kingdom kingdom) {
        return relations.stream().filter(r -> r.kingdom.equals(kingdom) || r.otherKingdom.equals(kingdom)).collect(Collectors.toList());
    }

    public Relation getRelation(Kingdom kingdom, Kingdom other) {
        return relations.stream().filter(r -> (r.kingdom.equals(kingdom) && r.otherKingdom.equals(other))
                || (r.kingdom.equals(other) && r.otherKingdom.equals(kingdom))).filter(r -> !r.isRequest()).findFirst().orElse(null);
    }

    public void setRelation(Kingdom kingdom, Kingdom other, RelationType type) {
        Relation oldrel = getRelation(kingdom, other);
        if ( oldrel != null ) {
            oldrel.delete();
            return;
        }

        BRelation newrel = createRelation(kingdom, other, type, false);
        relations.add(newrel);
        plugin.getScheduler().async().execute(newrel::save);
    }

    public void addRelationRequest(Kingdom kingdom, Kingdom other, RelationType type) {
        BRelation rel = createRelation(kingdom, other, type, true);
        relations.add(rel);
        plugin.getScheduler().async().execute(rel::save);
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
        relations.remove(rel);
        plugin.getScheduler().async().execute(rel::delete);
    }

    private BRelation createRelation(Kingdom kingdom, Kingdom other, RelationType type, boolean isRequest) {
        BRelation rel = new BRelation();
        rel.kingdom = (BKingdom) kingdom;
        rel.otherKingdom = (BKingdom) other;
        rel.relation = type.getId();
        rel.isRequest = isRequest;
        return rel;
    }

    private void reassign(Relation relation) {
        BRelation brel = (BRelation) relation;
        brel.kingdom = (BKingdom) getKingdom(brel.kingdom.getName());
        brel.otherKingdom = (BKingdom) getKingdom(brel.otherKingdom.getName());
    }

    // users

    public List<User> getOnlineUsers() {
        return new ArrayList<>(users);
    }

    public User getOnlineUser(String name) {
        return users.stream().filter(u -> u.name.equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public User getOnlineUser(UUID uuid) {
        return users.stream().filter(u -> u.id.equals(uuid.toString())).findFirst().orElse(null);
    }

    public CompletableFuture<List<User>> getUsers() {
        return plugin.getScheduler().makeAsyncFuture(() -> {
            List<BUser> users = new QBUser().findList();
            users.forEach(this::reassign);
            return users.stream().map((u) -> (User) u).collect(Collectors.toList());
        });
    }

    public CompletableFuture<User> getUser(String name) {
        User user = getOnlineUser(name);
        if ( user != null ) {
            return CompletableFuture.completedFuture(user);
        }

        return reassign(plugin.getScheduler().makeAsyncFuture(() ->
                new QBUser().name.eq(name).findOne()));
    }

    public CompletableFuture<User> getUser(UUID uuid) {
        User user = getOnlineUser(uuid);
        if ( user != null ) {
            return CompletableFuture.completedFuture(user);
        }

        return reassign(plugin.getScheduler().makeAsyncFuture(() ->
                new QBUser().id.eq(uuid.toString()).findOne()));
    }

    public User createUser(UUID uuid, String name) {
        BUser user = new BUser();
        user.id = uuid.toString();
        user.name = name;
        return user;
    }

    public void addOnlineUser(User user) {
        if ( !users.contains(user) ) {
            users.add((BUser) user);
        }
    }

    public void removeOnlineUser(User user) {
        users.remove(user);
    }

    private CompletableFuture<User> reassign(CompletableFuture<User> future) {
        return future.thenApply(u -> {
            if ( u != null ) {
                reassign(u);
            }
            return u;
        });
    }

    private void reassign(User user) {
        BUser buser = (BUser) user;
        if ( buser.kingdom != null ) {
            buser.kingdom = (BKingdom) getKingdom(buser.kingdom.getName());

            if ( buser.rank != null ){
                buser.rank = (BRank) buser.kingdom.getRank(buser.rank.getName());
            }
        }
        for ( BKingdomInvite ki : buser.kingdomInvites ) {
            ki.kingdom = (BKingdom) getKingdom(ki.kingdom.getName());
        }
    }

}
