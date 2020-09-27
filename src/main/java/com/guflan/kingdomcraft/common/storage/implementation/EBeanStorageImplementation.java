package com.guflan.kingdomcraft.common.storage.implementation;


import com.guflan.kingdomcraft.api.domain.Kingdom;
import com.guflan.kingdomcraft.api.domain.Player;
import com.guflan.kingdomcraft.api.domain.Rank;
import com.guflan.kingdomcraft.common.storage.StorageImplementation;
import io.ebean.Database;
import io.ebean.DatabaseFactory;
import io.ebean.config.DatabaseConfig;
import io.ebean.datasource.DataSourceConfig;
import io.ebean.migration.MigrationConfig;
import io.ebean.migration.MigrationRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EBeanStorageImplementation implements StorageImplementation {

    // TODO
    private Database database;

    public EBeanStorageImplementation(String url, String driver, String username, String password) {
        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        dataSourceConfig.setUsername(username);
        dataSourceConfig.setPassword(password);
        dataSourceConfig.setUrl(url);
        dataSourceConfig.setDriver(driver);

        // run migrations
        this.migrate(dataSourceConfig);

        DatabaseConfig config = new DatabaseConfig();
        config.setDataSourceConfig(dataSourceConfig);

        // create database object
        this.database = DatabaseFactory.create(config);
    }

    private void migrate(DataSourceConfig dbConfig) {
        MigrationConfig config = new MigrationConfig();
        config.setDbUsername(dbConfig.getUsername());
        config.setDbPassword(dbConfig.getPassword());
        config.setDbUrl(dbConfig.getUrl());
        config.setDbDriver(dbConfig.getDriver());

        MigrationRunner runner = new MigrationRunner(config);
        runner.run();
    }

    @Override
    public List<Player> getPlayers() {
        return null;
    }

    @Override
    public Player getPlayer(String name) {
        return null;
    }

    @Override
    public Player getPlayer(UUID uuid) {
        return null;
    }

    @Override
    public void savePlayer(Player player) {

    }

    @Override
    public List<Kingdom> getKingdoms() {
        return new ArrayList<>();
    }



    @Override
    public void saveKingdom(Kingdom kingdom) {

    }

    @Override
    public void saveRank(Rank rank) {

    }

    @Override
    public void deleteKingdom(Kingdom kingdom) {

    }
}
