package com.igufguf.kingdomcraft.objects;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class KingdomRank extends KingdomData {

    private final String name;

    public KingdomRank(String name, ConfigurationSection section) {
        this.name = name;

        for ( String key : section.getKeys(false) ) {
            if ( section.getConfigurationSection(key) == null ) {
                setData(key, section.get(key));
            }
        }
    }

    public String getName() {
        return name;
    }

    public String[] getPermissions() {
        if ( hasData("permissions") && getData("permissions") instanceof List) {
            List<String> list = ((List) getData("permissions"));
            return list.toArray(new String[list.size()]);
        }
        return new String[0];
    }

    public void save(FileConfiguration file, String path) {
        for ( String key : this.data.keySet() ) {
            if ( !file.contains(path + "." + key) ) {
                file.set(path + "." + key, this.data.get(key));
            } else if ( hasInLocalList("changes", key) ) {
                file.set(path + "." + key, this.data.get(key));
            }
        }
    }
}
