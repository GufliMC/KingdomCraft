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

import java.util.*;

public class EmptyConfiguration implements Configuration {

    static final EmptyConfiguration EMPTY = new EmptyConfiguration();

    private EmptyConfiguration() {}

    @Override
    public Object get(String path) {
        return null;
    }

    @Override
    public boolean contains(String path) {
        return false;
    }

    @Override
    public String getString(String path) {
        return null;
    }

    @Override
    public boolean getBoolean(String path) {
        return false;
    }

    @Override
    public int getInt(String path) {
        return 0;
    }

    @Override
    public double getDouble(String path) {
        return 0;
    }

    @Override
    public List<String> getStringList(String path) {
        return Collections.emptyList();
    }

    @Override
    public Configuration getConfigurationSection(String path) {
        return EMPTY;
    }

    @Override
    public Set<String> getKeys(boolean deep) {
        return Collections.emptySet();
    }
}
