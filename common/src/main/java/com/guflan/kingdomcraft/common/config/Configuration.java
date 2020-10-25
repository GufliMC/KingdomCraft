/*
 * This file is part of KingdomCraft.
 *
 * KingdomCraft is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * KingdomCraft is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with KingdomCraft. If not, see <https://www.gnu.org/licenses/>.
 */

package com.guflan.kingdomcraft.common.config;

import java.util.List;
import java.util.Set;

public interface Configuration {

    Object get(String path);

    boolean contains(String path);

    String getString(String path);

    boolean getBoolean(String path);

    int getInt(String path);

    double getDouble(String path);

    List<String> getStringList(String path);

    Configuration getConfigurationSection(String path);

    Set<String> getKeys(boolean deep);

}
