/*
 * This file is part of KingdomCraft.
 *
 * KingdomCraft is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * KingdomCraft is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with KingdomCraft. If not, see <https://www.gnu.org/licenses/>.
 */

package com.gufli.kingdomcraft.bukkit.config;

import com.gufli.kingdomcraft.common.config.Configuration;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.Set;

public class BukkitConfiguration implements Configuration {

    private final ConfigurationSection configurationSection;

    public BukkitConfiguration(ConfigurationSection configurationSection) {
        this.configurationSection = configurationSection;
    }

    @Override
    public Object get(String path) {
        return configurationSection.get(path);
    }

    @Override
    public boolean contains(String path) {
        return configurationSection.contains(path);
    }

    @Override
    public String getString(String path) {
        return configurationSection.getString(path);
    }

    @Override
    public boolean getBoolean(String path) {
        return configurationSection.getBoolean(path);
    }

    @Override
    public int getInt(String path) {
        return configurationSection.getInt(path);
    }

    @Override
    public double getDouble(String path) {
        return configurationSection.getDouble(path);
    }

    @Override
    public List<String> getStringList(String path) {
        return configurationSection.getStringList(path);
    }

    @Override
    public Configuration getConfigurationSection(String path) {
        ConfigurationSection cs = configurationSection.getConfigurationSection(path);
        return cs == null ? null : new BukkitConfiguration(cs);
    }

    @Override
    public Set<String> getKeys(boolean deep) {
        return configurationSection.getKeys(deep);
    }
}
