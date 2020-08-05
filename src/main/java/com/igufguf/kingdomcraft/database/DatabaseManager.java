package com.igufguf.kingdomcraft.database;

import io.ebean.Database;
import io.ebean.DatabaseFactory;
import io.ebean.annotation.Platform;
import io.ebean.config.DatabaseConfig;
import io.ebean.datasource.DataSourceConfig;
import io.ebean.dbmigration.DbMigration;
import io.ebean.migration.MigrationConfig;
import io.ebean.migration.MigrationRunner;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.sql.Connection;

public class DatabaseManager {

    private Plugin plugin;
    private Database database;

    public DatabaseManager(Plugin plugin, ConfigurationSection config) {
        this.plugin = plugin;

        ClassLoader originalContextClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(getClass().getClassLoader());

        // MIGRATE

        MigrationConfig migrationConfig = new MigrationConfig();
        migrationConfig.setDbUsername(config.getString("username"));
        migrationConfig.setDbPassword(config.getString("password"));
        migrationConfig.setDbDriver(config.getString("driver"));
        migrationConfig.setDbUrl(config.getString("url"));

        MigrationRunner runner = new MigrationRunner(migrationConfig);
        runner.run();
        plugin.getLogger().info("Database migrations ran!");

        // CONNECT

        DatabaseConfig serverConfig = new DatabaseConfig();
        serverConfig.setRegister(true);

        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        dataSourceConfig.setUsername(config.getString("username"));
        dataSourceConfig.setPassword(config.getString("password"));
        dataSourceConfig.setDriver(config.getString("driver"));
        dataSourceConfig.setUrl(config.getString("url"));
        dataSourceConfig.setIsolationLevel(Connection.TRANSACTION_SERIALIZABLE);

        serverConfig.setDataSourceConfig(dataSourceConfig);
        this.database = DatabaseFactory.create(serverConfig);

        Thread.currentThread().setContextClassLoader(originalContextClassLoader);
        plugin.getLogger().info("Connected with database!");

    }

    public Database getDatabase() {
        return this.database;
    }

    /**
     * Generate the DDL for the next DB migration.
     */
    public static void main(String[] args) throws IOException {

        DbMigration dbMigration = DbMigration.create();
        dbMigration.setPlatform(Platform.SQLITE);

        dbMigration.generateMigration();
    }

}
