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

package com.guflan.kingdomcraft.common.entity;

import com.guflan.kingdomcraft.api.entity.PlatformPlayer;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractPlatformPlayer implements PlatformPlayer {

    final Map<String, Object> cache = new HashMap<>();

    @Override
    public void set(String key, Object value) {
        cache.put(key, value);
    }

    @Override
    public boolean has(String key) {
        return cache.containsKey(key);
    }

    @Override
    public Object get(String key) {
        return cache.get(key);
    }

    @Override
    public <T> T get(String key, Class<T> clazz) {
        return (T) cache.get(key);
    }

}
