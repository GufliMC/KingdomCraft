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

package com.gufli.kingdomcraft.common.ebean;

import com.gufli.kingdomcraft.api.domain.*;
import com.gufli.kingdomcraft.api.entity.PlatformPlayer;
import com.gufli.kingdomcraft.common.KingdomCraftPlugin;
import com.gufli.kingdomcraft.common.ebean.beans.*;
import com.gufli.kingdomcraft.common.ebean.beans.query.QBKingdom;
import com.gufli.kingdomcraft.common.ebean.beans.query.QBRelation;
import com.gufli.kingdomcraft.common.ebean.beans.query.QBUser;
import io.ebean.DB;
import io.ebean.DatabaseFactory;
import io.ebean.Transaction;
import io.ebean.config.DatabaseConfig;
import io.ebean.datasource.DataSourceConfig;
import io.ebean.datasource.DataSourceFactory;
import io.ebean.datasource.DataSourcePool;
import io.ebean.migration.MigrationConfig;
import io.ebean.migration.MigrationRunner;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class StorageContext {

    public static final Set<BKingdom> kingdoms = new CopyOnWriteArraySet<>();
    public static final Set<BRelation> relations = new CopyOnWriteArraySet<>();

    public static final Map<PlatformPlayer, User> players = new ConcurrentHashMap<>();

    private boolean initialized = false;
    private final KingdomCraftPlugin plugin;

    public StorageContext(KingdomCraftPlugin plugin) {
        this.plugin = plugin;
    }

    public void init(String url, String driver, String username, String password) {
        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        dataSourceConfig.setUrl(url);
        dataSourceConfig.setDriver(driver);
        dataSourceConfig.setUsername(username);
        dataSourceConfig.setPassword(password);

        DataSourcePool pool;
        try {
            pool = DataSourceFactory.create("kingdomcraft", dataSourceConfig);

            // run migrations
            migrate(pool);

            // create database
            connect(pool);
        } catch (Exception ex) {
            plugin.log(ex.getMessage(), Level.SEVERE);
            return;
        }

        // load cache
        kingdoms.addAll(new QBKingdom().findList());

        new QBRelation().findList().forEach(rel ->  {
            reassign(rel);
            relations.add(rel);
        });

        initialized = true;
    }

    private void migrate(DataSourcePool pool) throws SQLException {
        MigrationConfig config = new MigrationConfig();

        Connection conn = pool.getConnection();
        String platform = conn.getMetaData().getDatabaseProductName().toLowerCase();
        config.setMigrationPath("dbmigration/" + platform);

        MigrationRunner runner = new MigrationRunner(config);
        runner.run(conn);
    }

    private void connect(DataSourcePool pool) {
        DatabaseConfig config = new DatabaseConfig();
        config.setDataSource(pool);
        config.setRegister(true);
        config.setDefaultServer(false);
        config.setName("kingdomcraft");

        config.addClass(BKingdom.class);
        config.addClass(BKingdomAttribute.class);
        config.addClass(BKingdomInvite.class);
        config.addClass(BRank.class);
        config.addClass(BRankAttribute.class);
        config.addClass(BRankPermissionGroup.class);
        config.addClass(BRelation.class);
        config.addClass(BUser.class);
        config.addClass(BUserChatChannel.class);
        config.addClass(PlatformLocationConverter.class);

        DatabaseFactory.create(config);
    }

    public boolean isInitialized() {
        return initialized;
    }

    public Set<Kingdom> getKingdoms() {
        return new HashSet<>(kingdoms);
    }

    public Kingdom getKingdom(String name) {
        return kingdoms.stream().filter(k -> k.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public Kingdom getKingdom(long id) {
        return kingdoms.stream().filter(k -> k.getId() == id).findFirst().orElse(null);
    }

    public Kingdom createKingdom(String name) {
        BKingdom kingdom = new BKingdom();
        kingdom.name = name;
        kingdoms.add(kingdom);
        return kingdom;
    }

    // relations

    public Set<Relation> getRelations(Kingdom kingdom) {
        return relations.stream()
                .filter(r -> !r.isRequest())
                .filter(r -> r.kingdom.equals(kingdom) || r.otherKingdom.equals(kingdom))
                .collect(Collectors.toSet());
    }

    public Relation getRelation(Kingdom kingdom, Kingdom other) {
        return relations.stream()
                .filter(r -> !r.isRequest())
                .filter(r -> (r.kingdom.equals(kingdom) && r.otherKingdom.equals(other))
                    || (r.kingdom.equals(other) && r.otherKingdom.equals(kingdom)))
                .findFirst().orElse(null);
    }

    public void setRelation(Kingdom kingdom, Kingdom other, RelationType type) {
        Relation oldrel = getRelation(kingdom, other);
        if ( oldrel != null ) {
            oldrel.delete();
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
        return relations.stream()
                .filter(Relation::isRequest)
                .filter(r -> r.kingdom.equals(kingdom) && r.otherKingdom.equals(other))
                .findFirst().orElse(null);
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

    public Set<User> getOnlineUsers() {
        return new HashSet<>(players.values());
    }

    public User getOnlineUser(String name) {
        return players.values().stream().filter(u -> u.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public User getOnlineUser(UUID uuid) {
        return players.values().stream().filter(u -> u.getUniqueId().equals(uuid)).findFirst().orElse(null);
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

    public void update(User user, PlatformPlayer player) {
        BUser buser = (BUser) user;
        buser.update(player.getUniqueId(), player.getName());
        saveAsync(Collections.singleton(buser));
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

    // players

    public void addPlayer(PlatformPlayer player, User user) {
        players.put(player, user);
    }

    public void removePlayer(PlatformPlayer player) {
        players.remove(player);
    }

    public Set<PlatformPlayer> getPlayers() {
        return new HashSet<>(players.keySet());
    }

    public PlatformPlayer getPlayer(UUID uuid) {
        return players.keySet().stream().filter(p -> p.getUniqueId().equals(uuid)).findFirst().orElse(null);
    }

    public PlatformPlayer getPlayer(User user) {
        return getPlayer(user.getUniqueId());
    }

    public PlatformPlayer getPlayer(String name) {
        return players.keySet().stream().filter(p -> p.getName().equalsIgnoreCase(name)).findFirst().orElse(null);

    }

    //

    public CompletableFuture<Void> saveAsync(Collection<Model> models) {
        return plugin.getScheduler().makeAsyncFuture(() -> {
            save(models);
        }).handle((v, ex) -> {
            if ( ex != null ) {
                ex.printStackTrace();
                plugin.log(ex.getMessage(), Level.SEVERE);
            }
            return v;
        });
    }

    private void save(Collection<Model> models) {
        try (Transaction transaction = DB.byName("kingdomcraft").beginTransaction()) {
            for (Model m : models) {
                m.save();
            }

            transaction.commit();
        }
    }

    public CompletableFuture<Void> deleteAsync(Collection<Model> models) {
        return plugin.getScheduler().makeAsyncFuture(() -> {
            delete(models);
        }).handle((v, ex) -> {
            if ( ex != null ) {
                ex.printStackTrace();
                plugin.log(ex.getMessage(), Level.SEVERE);
            }
            return v;
        });
    }

    private void delete(Collection<Model> models) {
        try (Transaction transaction = DB.byName("kingdomcraft").beginTransaction()) {
            for (Model m : models) {
                m.delete();
            }

            transaction.commit();
        }
    }

}
