package com.igufguf.kingdomcraft.api.models.kingdom;

import com.igufguf.kingdomcraft.api.models.storage.Storable;
import com.igufguf.kingdomcraft.api.models.storage.MemoryHolder;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.igufguf.kingdomcraft.utils.KingdomUtils.formatString;

/**
 * Copyrighted 2018 iGufGuf
 *
 * This file is part of KingdomCraft.
 *
 * Kingdomcraft is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * KingdomCraft is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with KingdomCraft.  If not, see <http://www.gnu.org/licenses/>.
 *
 **/
public class KingdomRank extends MemoryHolder {

    private final Storable kingdomData = new Storable();

    private final Kingdom kingdom;

    private final String name;
    private final boolean isDefault;

    private final Map<String, Boolean> permissions = new HashMap<>();

    private String display;
    private String prefix;
    private String suffix;

    public KingdomRank(Kingdom kingdom, String name, boolean isDefault) {
        this.kingdom = kingdom;

        this.name = name;
        this.isDefault = isDefault;
    }

    // getters

    public Kingdom getKingdom() {
        return kingdom;
    }

    public String getName() {
        return name;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public String getDisplay() {
        return display != null ? display : getName();
    }

    public String getPrefix() {
        return prefix != null ? formatString(prefix) : null;
    }

    public String getSuffix() {
        return suffix != null ? formatString(suffix) : null;
    }

    public Storable getKingdomData() {
        return kingdomData;
    }

    // permissions

    /** Should only be used when loading from configuration, changes here will not save through plugin restarts **/
    public void setPermissions(List<String> permissions) {
        this.permissions.clear();

        for ( String perm : permissions ) {
            if ( perm.startsWith("-") ) {
                this.permissions.put(perm.substring(1).trim(), false);
            } else {
                this.permissions.put(perm.trim(), true);
            }
        }
    }

    public Map<String, Boolean> getPermissions() {
        return permissions;
    }

    // save

    public void saveData(ConfigurationSection data) {
        kingdomData.save(data);
    }

    // load

    // data (can be changed at runtime)
    public void loadData(ConfigurationSection data) {
        kingdomData.load(data);
    }

    // configuration (can't be changed at runtime)
    public static KingdomRank load(ConfigurationSection data, Kingdom kingdom, String name) {
        boolean defaultRank = data.getBoolean("default");

        KingdomRank rank = new KingdomRank(kingdom, name, defaultRank);

        rank.prefix = data.getString("prefix");
        rank.suffix = data.getString("suffix");
        rank.display = data.getString("display");

        rank.setPermissions(data.getStringList("permissions"));

        return rank;
    }

}
