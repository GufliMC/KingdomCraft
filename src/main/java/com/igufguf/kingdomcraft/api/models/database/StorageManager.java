package com.igufguf.kingdomcraft.api.models.database;

import com.igufguf.kingdomcraft.api.models.kingdom.KingdomUser;
import org.apache.commons.io.FileUtils;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.nio.file.Files;
import java.util.Base64;

/**
 * Created by Joris on 17/08/2018 in project KingdomCraft.
 */
public class StorageManager {

    private final File storageFile;
    private final FileConfiguration storageData;

    private final boolean unreadable;

    public StorageManager(File storageFile) {
        this(storageFile, true);
    }

    public StorageManager(File storageFile, boolean unreadable) {
        this.unreadable = unreadable;

        this.storageData = new YamlConfiguration();
        this.storageFile = storageFile;

        if ( !this.storageFile.exists() ) {

            if ( !this.storageFile.getParentFile().exists() )
                this.storageFile.getParentFile().mkdirs();

            try {
                this.storageFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // no need to convert data because there is no data
            return;
        } else {
            // create a backup of the data file, in case a misconfiguration messes up the original data file
            try {
                FileUtils.copyFile(this.storageFile, new File(storageFile + ".prev"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if ( !unreadable ) {

            // no need to convert from base64
            try {
                this.storageData.load(this.storageFile);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }

            return;
        }

        // data is unreadable because it is base64
        // convert base64 to yaml
        try (
                FileInputStream fis = new FileInputStream(this.storageFile)
        ) {
            int length = fis.available();
            if ( length != 0 ) {
                byte[] data = new byte[length];
                fis.read(data);

                String yaml = new String(Base64.getDecoder().decode(data));
                this.storageData.loadFromString(yaml);
            }
        } catch (InvalidConfigurationException | IOException e) {
            e.printStackTrace();
        }
    }

    public FileConfiguration getStorageData() {
        return storageData;
    }

    public void save() {
        String yaml = this.storageData.saveToString();

        byte[] data;
        if ( unreadable ) {
            // make data unreadable
            data = Base64.getEncoder().encode(yaml.getBytes());
        } else {
            // save data as play yaml
            data = yaml.getBytes();
        }

        try (
                FileOutputStream fos = new FileOutputStream(this.storageFile)
        ) {
            fos.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
