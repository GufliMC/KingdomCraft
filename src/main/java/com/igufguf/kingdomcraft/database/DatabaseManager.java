package com.igufguf.kingdomcraft.database;

import io.ebean.Database;
import io.ebean.DatabaseFactory;
import io.ebean.config.DatabaseConfig;
import io.ebean.config.dbplatform.mysql.MySqlPlatform;
import io.ebean.config.dbplatform.postgres.PostgresPlatform;
import io.ebean.config.dbplatform.sqlite.SQLitePlatform;
import io.ebean.datasource.DataSourceConfig;
import io.ebean.dbmigration.DbMigration;
import io.ebean.migration.MigrationConfig;
import io.ebean.migration.MigrationRunner;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;

public class DatabaseManager {

    private Plugin plugin;
    private Database database;

    public DatabaseManager(Plugin plugin, ConfigurationSection config) {
        this.plugin = plugin;

        // WARNING
        // For Ebean to work we have to change the classloader of this thread to the one used by this plugin.
        // This might cause problems in the future. Unfortunately I haven't been able to find a better solution.
        Thread.currentThread().setContextClassLoader(getClass().getClassLoader());

        // MIGRATE

        MigrationConfig migrationConfig = new MigrationConfig();
        migrationConfig.setDbUsername(config.getString("username"));
        migrationConfig.setDbPassword(config.getString("password"));
        migrationConfig.setDbDriver(config.getString("driver"));
        migrationConfig.setDbUrl(config.getString("url"));

        MigrationRunner runner = new MigrationRunner(migrationConfig);
        runner.run();

        // CONNECT

        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        dataSourceConfig.setUsername(config.getString("username"));
        dataSourceConfig.setPassword(config.getString("password"));
        dataSourceConfig.setDriver(config.getString("driver"));
        dataSourceConfig.setUrl(config.getString("url"));
        dataSourceConfig.setIsolationLevel(Connection.TRANSACTION_SERIALIZABLE);
        dataSourceConfig.setAutoCommit(true);

        DatabaseConfig serverConfig = new DatabaseConfig();
        serverConfig.setDataSourceConfig(dataSourceConfig);
        serverConfig.setRegister(true);

        this.database = DatabaseFactory.create(serverConfig);

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
        dbMigration.addDatabasePlatform(new SQLitePlatform(), "sqlite");
        dbMigration.addDatabasePlatform(new MySqlPlatform(), "mysql");
        dbMigration.addDatabasePlatform(new PostgresPlatform(), "postgresql");

        dbMigration.generateMigration();
    }

}
