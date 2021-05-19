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
import com.gufli.kingdomcraft.common.KingdomCraftImpl;
import com.gufli.kingdomcraft.common.KingdomCraftPlugin;
import com.gufli.kingdomcraft.common.commands.admin.SqlDumpCommand;
import com.gufli.kingdomcraft.common.ebean.beans.*;
import com.gufli.kingdomcraft.common.ebean.beans.query.QBKingdom;
import com.gufli.kingdomcraft.common.ebean.beans.query.QBRelation;
import com.gufli.kingdomcraft.common.ebean.beans.query.QBUser;
import io.ebean.DB;
import io.ebean.Database;
import io.ebean.DatabaseFactory;
import io.ebean.Transaction;
import io.ebean.annotation.Platform;
import io.ebean.config.DatabaseConfig;
import io.ebean.datasource.DataSourceConfig;
import io.ebean.datasource.DataSourceFactory;
import io.ebean.datasource.DataSourcePool;
import io.ebean.migration.MigrationConfig;
import io.ebean.migration.MigrationRunner;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class StorageContext {

    public static BKingdom templateKingdom;
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

        // load template
        for ( Kingdom kd : kingdoms ) {
            if ( kd.getId() == -1 ) {
                templateKingdom = (BKingdom) kd;
                templateKingdom.name = "template";
                kingdoms.remove(kd);
                break;
            }
        }

        // create template
        if ( templateKingdom == null ) {
            templateKingdom = new BKingdom();
            templateKingdom.id = -1;
            templateKingdom.name = "template";
            templateKingdom.setDisplay("&f{kingdom_name}");
            templateKingdom.setPrefix("&7[&f{kingdom_name}&7]");

            Rank member = templateKingdom.createRank("member");
            member.setDisplay("&7Member");
            member.setLevel(0);
            member.setPrefix("&7[Member]");

            Rank duke = templateKingdom.createRank("duke");
            duke.setDisplay("&2Duke");
            duke.setLevel(20);
            duke.setPrefix("&7[&2Duke&7]");

            Rank king = templateKingdom.createRank("king");
            king.setDisplay("&6King");
            king.setLevel(30);
            king.setPrefix("&7[&6King&7]");

            saveAsync(templateKingdom, member, duke, king).thenRun(() -> {
                templateKingdom.setDefaultRank(member);
                saveAsync(templateKingdom);
            });
        }

        // load relations
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
        config.addClass(ItemConverter.class);

        DatabaseFactory.create(config);
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void stop() {
        DB.byName("kingdomcraft").shutdown(true, true);
    }

    public void registerDumpCommand(KingdomCraftImpl kdc) {
        Database db = DB.byName("kingdomcraft");
        if ( db.getPlatform() == Platform.H2 ) {
            kdc.getCommandManager().addCommand(new SqlDumpCommand(kdc));
        }
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

    public Kingdom getTemplateKingdom() {
        return templateKingdom;
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
        // remove old relation
        Relation oldrel = getRelation(kingdom, other);
        if ( oldrel != null ) {
            relations.remove(oldrel);
            oldrel.delete();
        }

        // remove relation requests
        Set<Relation> requests = relations.stream()
                .filter(Relation::isRequest)
                .filter(r -> (r.kingdom.equals(kingdom) && r.otherKingdom.equals(other))
                        || (r.otherKingdom.equals(kingdom) && r.kingdom.equals(other)))
                .collect(Collectors.toSet());
        relations.removeAll(requests);
        deleteAsync(requests);

        if ( type == RelationType.NEUTRAL ) {
            return;
        }

        BRelation newrel = createRelation(kingdom, other, type, false);
        relations.add(newrel);
        saveAsync(newrel);
    }

    public void addRelationRequest(Kingdom kingdom, Kingdom other, RelationType type) {
        removeRelationRequest(kingdom, other);
        BRelation rel = createRelation(kingdom, other, type, true);
        relations.add(rel);
        saveAsync(rel);
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
        deleteAsync(rel);
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

    public void clearUsers() {
        new QBUser().delete();
        for ( User user : players.values() ) {
            user.setKingdom(null);
            ((BUser) user).insert();
        }
        for ( BKingdom kingdom : kingdoms ) {
            kingdom.resetMemberCount();
        }
        saveAsync(kingdoms);
    }

    public void purgeUsers() {
        new QBUser().updatedAt.before(Instant.now().minus(2, ChronoUnit.WEEKS)).delete();
    }

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
                new QBUser().name.ieq(name).findOne()));
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

    public void updateName(User user, String name) {
        BUser buser = (BUser) user;
        buser.setName(name);
        saveAsync(buser);
    }

    public void updateUUID(User user, UUID id) {
        BUser buser = (BUser) user;
        new QBUser().id.eq(buser.id).asUpdate().set("id", id.toString()).update();
        buser.setUUID(id);
        saveAsync(buser);
    }

    public void login(User user) {
        BUser buser = (BUser) user;
        buser.setLastOnlineAt(Instant.now());
        saveAsync(buser);
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

    public CompletableFuture<Void> saveAsync(Model... models) {
        return saveAsync(Arrays.asList(models));
    }

    public <T extends Model> CompletableFuture<Void> saveAsync(Collection<T> models) {
        return plugin.getScheduler().makeAsyncFuture(() -> {
            save(models);
        }).exceptionally(ex -> {
            if ( ex != null ) {
                ex.printStackTrace();
                plugin.log(ex.getMessage(), Level.SEVERE);
            }
            return null;
        });
    }

    private <T extends Model> void save(Collection<T> models) {
        try (Transaction transaction = DB.byName("kingdomcraft").beginTransaction()) {
            for (Model m : models) {
                m.save();
            }

            transaction.commit();
        }
    }

    public CompletableFuture<Void> deleteAsync(Model... models) {
        return deleteAsync(Arrays.asList(models));
    }

    public <T extends Model> CompletableFuture<Void> deleteAsync(Collection<T> models) {
        return plugin.getScheduler().makeAsyncFuture(() -> {
            delete(models);
        }).exceptionally(ex -> {
            if ( ex != null ) {
                ex.printStackTrace();
                plugin.log(ex.getMessage(), Level.SEVERE);
            }
            return null;
        });
    }

    private <T extends Model> void delete(Collection<T> models) {
        try (Transaction transaction = DB.byName("kingdomcraft").beginTransaction()) {
            for (Model m : models) {
                m.delete();
            }

            transaction.commit();
        }
    }

}
