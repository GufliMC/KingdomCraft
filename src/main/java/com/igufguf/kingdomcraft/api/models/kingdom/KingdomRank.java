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
    private final boolean isLeader;

    private final Map<String, Boolean> permissions = new HashMap<>();

    private String display;
    private String prefix;
    private String suffix;

    public KingdomRank(Kingdom kingdom, String name, boolean isDefault, boolean isLeader) {
        this.kingdom = kingdom;

        this.name = name;
        this.isDefault = isDefault;
        this.isLeader = isLeader;
    }

    public KingdomRank(KingdomRank rank, Kingdom kingdom) {
        this.kingdom = kingdom;
        this.name = rank.name;
        this.isDefault = rank.isDefault;
        this.isLeader = rank.isLeader;
        this.permissions.putAll(rank.permissions);
        this.display = rank.display;
        this.prefix = rank.prefix;
        this.suffix = rank.suffix;
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

    public boolean isLeader() {
        return isLeader;
    }

    public String getDisplay() {
        return display != null ? formatString(display) : getName();
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

    private void setPermissions(List<String> permissions) {
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
        ConfigurationSection ext = data.getConfigurationSection("ext");
        if ( ext == null ) {
            ext = data.createSection("ext");
        }

        kingdomData.save(ext);
        data.set("ext", ext);
    }

    // load

    // data (can be changed at runtime)
    public void loadData(ConfigurationSection data) {

        // load extra data
        if ( data.contains("ext") && data.get("ext") instanceof ConfigurationSection) {
            kingdomData.load(data.getConfigurationSection("ext"));
        }
    }

    // configuration (can't be changed at runtime)
    public static KingdomRank load(ConfigurationSection data, Kingdom kingdom, String name) {
        boolean defaultRank = data.getBoolean("default");
        boolean leaderRank = data.getBoolean("leader");

        KingdomRank rank = new KingdomRank(kingdom, name, defaultRank, leaderRank);

        rank.prefix = data.getString("prefix");
        rank.suffix = data.getString("suffix");
        rank.display = data.getString("display");

        rank.setPermissions(data.getStringList("permissions"));

        return rank;
    }

}
