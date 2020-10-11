package com.guflan.kingdomcraft.common.ebean;

import com.guflan.kingdomcraft.api.KingdomCraftPlugin;
import com.guflan.kingdomcraft.api.domain.Kingdom;
import com.guflan.kingdomcraft.api.domain.Rank;
import com.guflan.kingdomcraft.api.domain.Relation;
import com.guflan.kingdomcraft.api.domain.User;
import com.guflan.kingdomcraft.api.storage.Storage;
import com.guflan.kingdomcraft.common.ebean.beans.BKingdom;
import com.guflan.kingdomcraft.common.ebean.beans.BRank;
import com.guflan.kingdomcraft.common.ebean.beans.BUser;
import com.guflan.kingdomcraft.common.ebean.beans.query.QBKingdom;
import com.guflan.kingdomcraft.common.ebean.beans.query.QBRelation;
import com.guflan.kingdomcraft.common.ebean.beans.query.QBUser;
import io.ebean.DatabaseFactory;
import io.ebean.annotation.Transactional;
import io.ebean.config.DatabaseConfig;
import io.ebean.datasource.DataSourceConfig;
import io.ebean.migration.MigrationConfig;
import io.ebean.migration.MigrationException;
import io.ebean.migration.MigrationRunner;

import java.sql.SQLException;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;

public class EBeanStorage implements Storage {

    private final KingdomCraftPlugin plugin;

    public EBeanStorage(KingdomCraftPlugin plugin) {
        this.plugin = plugin;
    }

    public void init(String url, String driver, String username, String password) {
        // run migrations
        try {
            migrate(url, driver, username, password);
        } catch (MigrationException ex) {
            if ( ex.getCause() != null ) {
                ex.getCause().printStackTrace();
                //plugin.log(ex.getCause().getMessage(), Level.SEVERE);
            } else {
                ex.printStackTrace();
            }
            return;
        }

        // create database
        connect(url, driver, username, password);
    }

    private void migrate(String url, String driver, String username, String password) {
        MigrationConfig config = new MigrationConfig();
        config.setDbUrl(url);
        config.setDbDriver(driver);
        config.setDbUsername(username);
        config.setDbPassword(password);

        try {
            String platform = config.createConnection().getMetaData().getDatabaseProductName().toLowerCase();
            config.setMigrationPath("dbmigration/" + platform);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        MigrationRunner runner = new MigrationRunner(config);
        runner.run();
    }

    private void connect(String url, String driver, String username, String password) {
        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        dataSourceConfig.setUrl(url);
        dataSourceConfig.setDriver(driver);
        dataSourceConfig.setUsername(username);
        dataSourceConfig.setPassword(password);

        DatabaseConfig config = new DatabaseConfig();
        config.setDataSourceConfig(dataSourceConfig);
        config.setRegister(true);

        DatabaseFactory.create(config);
    }

    @Override
    public CompletableFuture<Set<Kingdom>> getKingdoms() {
        return makeFuture(() -> new QBKingdom().findList().stream().map(k -> (Kingdom) k).collect(Collectors.toSet()));
    }

    @Override
    public Kingdom createKingdom(String name) {
        BKingdom kingdom = new BKingdom();
        kingdom.name = name;
        return kingdom;
    }


    @Override
    public CompletableFuture<Void> delete(Kingdom kingdom) {
        return makeFuture(() -> {
            ((BKingdom) kingdom).delete();
        });
    }

    @Override
    public CompletableFuture<Void> delete(Rank rank) {
        return makeFuture(() -> {
            ((BRank) rank).delete();
        });
    }

    @Override
    public CompletableFuture<Void> save(Kingdom kingdom) {
        return makeFuture(() -> ((BKingdom) kingdom).save());
    }

    @Override
    public CompletableFuture<Void> save(Rank rank) {
        return makeFuture(() -> ((BRank) rank).save());
    }

    // users

    @Override
    public User createUser(UUID uuid, String name) {
        BUser user = new BUser();
        user.id = uuid.toString();
        user.name = name;
        return user;
    }

    @Override
    public CompletableFuture<Set<User>> getUsers() {
        return makeFuture(() -> new QBUser().findList().stream().map(u -> (User) u).collect(Collectors.toSet()));
    }

    @Override
    public CompletableFuture<User> getUser(String name) {
        return makeFuture(() -> new QBUser().name.eq(name).findOne());
    }

    @Override
    public CompletableFuture<User> getUser(UUID uuid) {
        return makeFuture(() -> new QBUser().id.eq(uuid.toString()).findOne());
    }

    @Override
    public CompletableFuture<Void> save(User user) {
        return makeFuture(() -> ((BUser) user).save());
    }

    // ----

    private <T> CompletableFuture<T> makeFuture(Callable<T> supplier) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return supplier.call();
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        }, plugin.getScheduler().async());
    }

    private CompletableFuture<Void> makeFuture(Runnable runnable) {
        return CompletableFuture.runAsync(() -> {
            try {
                runnable.run();
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        }, plugin.getScheduler().async());
    }

}
