package com.igufguf.kingdomcraft.api.models.database;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

/**
 * Created by Joris on 17/08/2018 in project KingdomCraft.
 */
public class Configurable {

    private final File configFile;
    private final FileConfiguration configData;

    public Configurable(File configFile) {
        this.configData = new YamlConfiguration();
        this.configFile = configFile;
        loadConfig();
    }

    public void loadConfig() {
        if ( !this.configFile.exists() ) return;

        // load config file
        try {
            this.configData.load(this.configFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public FileConfiguration getConfiguration() {
        return configData;
    }

    public File getConfigFile() {
        return configFile;
    }

    public void saveConfiguration() {
        try {
            this.configData.save(this.configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
