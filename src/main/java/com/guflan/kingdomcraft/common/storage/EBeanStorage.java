package com.guflan.kingdomcraft.common.storage;

import com.guflan.kingdomcraft.api.KingdomCraft;
import com.guflan.kingdomcraft.api.domain.Kingdom;
import com.guflan.kingdomcraft.api.domain.User;
import com.guflan.kingdomcraft.api.scheduler.AbstractScheduler;
import com.guflan.kingdomcraft.api.storage.Storage;
import com.guflan.kingdomcraft.common.ebean.beans.BKingdom;
import com.guflan.kingdomcraft.common.ebean.beans.BUser;
import com.guflan.kingdomcraft.common.ebean.beans.query.QBKingdom;
import com.guflan.kingdomcraft.common.ebean.beans.query.QBUser;
import io.ebean.Database;
import io.ebean.DatabaseFactory;
import io.ebean.config.DatabaseConfig;
import io.ebean.datasource.DataSourceConfig;
import io.ebean.migration.MigrationConfig;
import io.ebean.migration.MigrationRunner;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;

public class EBeanStorage implements Storage {

    private final AbstractScheduler scheduler;
    private Database database;

    public EBeanStorage(AbstractScheduler scheduler, String url, String driver, String username, String password) {
        this.scheduler = scheduler;

        ClassLoader originalContextClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(getClass().getClassLoader());

        // run migrations
        migrate(url, driver, username, password);

        // create database
        connect(url, driver, username, password);

        Thread.currentThread().setContextClassLoader(originalContextClassLoader);
    }

    private void migrate(String url, String driver, String username, String password) {
        MigrationConfig config = new MigrationConfig();
        config.setDbUrl(url);
        config.setDbDriver(driver);
        config.setDbUsername(username);
        config.setDbPassword(password);

        MigrationRunner runner = new MigrationRunner(config);
        runner.run();
    }

    private void connect(String url, String driver, String username, String password) {
        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        dataSourceConfig.setUrl(url);
        dataSourceConfig.setDriver(driver);
        dataSourceConfig.setUsername(username);
        dataSourceConfig.setPassword(password);

        io.ebean.config.DatabaseConfig config = new DatabaseConfig();
        config.setDataSourceConfig(dataSourceConfig);
        this.database = DatabaseFactory.create(config);
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
    public CompletableFuture<Void> save(Kingdom kingdom) {
        return makeFuture(() -> ((BKingdom) kingdom).save());
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
        }, scheduler.async());
    }

    private CompletableFuture<Void> makeFuture(Runnable runnable) {
        return CompletableFuture.runAsync(() -> {
            try {
                runnable.run();
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        }, scheduler.async());
    }

}
