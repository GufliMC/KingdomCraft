package com.guflan.kingdomcraft.common.ebean;

import com.guflan.kingdomcraft.api.KingdomCraftPlugin;
import com.guflan.kingdomcraft.api.domain.DomainStorage;
import com.guflan.kingdomcraft.api.domain.models.Kingdom;
import com.guflan.kingdomcraft.api.domain.models.Rank;
import com.guflan.kingdomcraft.api.domain.models.Relation;
import com.guflan.kingdomcraft.api.domain.models.User;
import com.guflan.kingdomcraft.common.ebean.beans.BKingdom;
import com.guflan.kingdomcraft.common.ebean.beans.BRank;
import com.guflan.kingdomcraft.common.ebean.beans.BRelation;
import com.guflan.kingdomcraft.common.ebean.beans.BUser;
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

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class EBeanStorage implements DomainStorage {

    private final KingdomCraftPlugin plugin;

    public EBeanStorage(KingdomCraftPlugin plugin) {
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

    // interface

    @Override
    public CompletableFuture<List<Kingdom>> getKingdoms() {
        return plugin.getScheduler().makeAsyncFuture(() ->
                new QBKingdom().findList().stream().map(k -> (Kingdom) k).collect(Collectors.toList()));
    }

    @Override
    public CompletableFuture<Kingdom> getKingdom(String name) {
        return plugin.getScheduler().makeAsyncFuture(() -> new QBKingdom().name.eq(name).findOne());
    }

    @Override
    public CompletableFuture<Void> delete(Kingdom kingdom) {
        return plugin.getScheduler().makeAsyncFuture(() -> {
            ((BKingdom) kingdom).delete();
        });
    }

    @Override
    public CompletableFuture<Void> save(Kingdom kingdom) {
        return plugin.getScheduler().makeAsyncFuture(() -> ((BKingdom) kingdom).save());
    }

    // ranks

    @Override
    public CompletableFuture<Void> delete(Rank rank) {
        return plugin.getScheduler().makeAsyncFuture(() -> {
            ((BRank) rank).delete();
        });
    }

    @Override
    public CompletableFuture<Void> save(Rank rank) {
        return plugin.getScheduler().makeAsyncFuture(() -> ((BRank) rank).save());
    }

    // relations

    @Override
    public CompletableFuture<List<Relation>> getRelations() {
        return plugin.getScheduler().makeAsyncFuture(() -> new QBRelation().findList().stream().map(r -> (Relation) r).collect(Collectors.toList()));
    }

    @Override
    public CompletableFuture<List<Relation>> getRelations(Kingdom kingdom) {
        return plugin.getScheduler().makeAsyncFuture(() -> new QBRelation().kingdom.eq((BKingdom) kingdom).or().otherKingdom.eq((BKingdom) kingdom)
                .findList().stream().map(r -> (Relation) r).collect(Collectors.toList()));
    }

    @Override
    public CompletableFuture<Void> save(Relation relation) {
        return plugin.getScheduler().makeAsyncFuture(() -> {
            ((BRelation) relation).save();
        });
    }

    @Override
    public CompletableFuture<Void> delete(Relation relation) {
        return plugin.getScheduler().makeAsyncFuture(() -> {
            ((BRelation) relation).delete();
        });
    }

    // users

    @Override
    public CompletableFuture<List<User>> getUsers() {
        return plugin.getScheduler().makeAsyncFuture(() -> new QBUser().findList().stream().map(u -> (User) u).collect(Collectors.toList()));
    }

    @Override
    public CompletableFuture<User> getUser(String name) {
        return plugin.getScheduler().makeAsyncFuture(() -> new QBUser().name.eq(name).findOne());
    }

    @Override
    public CompletableFuture<User> getUser(UUID uuid) {
        return plugin.getScheduler().makeAsyncFuture(() -> new QBUser().id.eq(uuid.toString()).findOne());
    }

    @Override
    public CompletableFuture<Void> save(User user) {
        return plugin.getScheduler().makeAsyncFuture(() -> ((BUser) user).save());
    }

    @Override
    public CompletableFuture<Void> delete(User user) {
        return plugin.getScheduler().makeAsyncFuture(() -> {
            ((BUser) user).delete();
        });
    }

}
