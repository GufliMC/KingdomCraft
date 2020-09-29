package com.guflan.kingdomcraft.common.storage.implementation;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.guflan.kingdomcraft.api.KingdomCraftPlugin;
import com.guflan.kingdomcraft.api.domain.Kingdom;
import com.guflan.kingdomcraft.api.domain.Player;
import com.guflan.kingdomcraft.api.domain.Rank;
import com.guflan.kingdomcraft.common.ebean.beans.BKingdom;
import com.guflan.kingdomcraft.common.ebean.beans.BPlayer;
import com.guflan.kingdomcraft.common.ebean.beans.BRank;
import com.guflan.kingdomcraft.common.ebean.beans.query.QBKingdom;
import com.guflan.kingdomcraft.common.ebean.beans.query.QBPlayer;
import com.guflan.kingdomcraft.common.storage.StorageImplementation;
import io.ebean.Database;
import io.ebean.DatabaseFactory;
import io.ebean.config.DatabaseConfig;
import io.ebean.datasource.DataSourceConfig;
import io.ebean.migration.MigrationConfig;
import io.ebean.migration.MigrationRunner;

import java.util.*;

public class EBeanStorageImplementation implements StorageImplementation {

    private KingdomCraftPlugin plugin;
    private Database database;

    public EBeanStorageImplementation(KingdomCraftPlugin plugin, String url, String driver, String username, String password) {
        this.plugin = plugin;

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
        plugin.log("Database setup completed.");
    }

    private void connect(String url, String driver, String username, String password) {
        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        dataSourceConfig.setUrl(url);
        dataSourceConfig.setDriver(driver);
        dataSourceConfig.setUsername(username);
        dataSourceConfig.setPassword(password);

        DatabaseConfig config = new DatabaseConfig();
        config.setDataSourceConfig(dataSourceConfig);
        this.database = DatabaseFactory.create(config);
    }

    @Override
    public List<Player> getPlayers() {
        return new ArrayList<>(new QBPlayer().findList());
    }

    @Override
    public Player getPlayer(String name) {
        return new QBPlayer().name.eq(name).findOne();
    }

    @Override
    public Player getPlayer(UUID uuid) {
        return new QBPlayer().id.eq(uuid.toString()).findOne();
    }

    @Override
    public void savePlayer(Player player) {
        if ( !(player instanceof BPlayer) ) {
            // TODO error
        }

        BPlayer bp = (BPlayer) player;
        bp.save();
    }

    @Override
    public Player createPlayer(UUID uuid, String name) {
        return new BPlayer(uuid.toString(), name);
    }

    @Override
    public List<Kingdom> getKingdoms() {
        return new ArrayList<>(new QBKingdom().findList());
    }

    @Override
    public void saveKingdom(Kingdom kingdom) {
        if ( !(kingdom instanceof BKingdom) ) {
            // TODO error
        }

        BKingdom bk = (BKingdom) kingdom;
        bk.save();
    }

    @Override
    public Kingdom createKingdom(String name) {
        return new BKingdom(name);
    }

    @Override
    public void deleteKingdom(Kingdom kingdom) {
        if ( !(kingdom instanceof BKingdom) ) {
            // TODO error
        }

        BKingdom bk = (BKingdom) kingdom;
        bk.delete();
    }

    @Override
    public void saveRank(Rank rank) {
        if ( !(rank instanceof BRank) ) {
            // TODO error
        }

        BRank br = (BRank) rank;
        br.save();
    }

    @Override
    public Rank createRank(String name, Kingdom kingdom) {
        if ( !(kingdom instanceof BKingdom) ) {
            // TODO error
        }

        return new BRank(name, (BKingdom) kingdom);
    }

    @Override
    public void deleteRank(Rank rank) {
        if ( !(rank instanceof BRank) ) {
            // TODO error
        }

        BRank br = (BRank) rank;
        br.delete();
    }

}
