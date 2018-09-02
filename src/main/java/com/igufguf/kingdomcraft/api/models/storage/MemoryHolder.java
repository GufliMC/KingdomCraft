package com.igufguf.kingdomcraft.api.models.storage;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
public abstract class MemoryHolder {

    // memory
    public final Map<String, Object> memory = new ConcurrentHashMap<>();

    public final Object getMemory(String key) {
        return memory.get(key);
    }

    public final <T> T getMemory(String key, Class<T> type) {
        return (T) getMemory(key);
    }

    public final <T> List<T> getMemoryList(String key, Class<T> type) {
        return (List<T>) getMemory(key);
    }

    public final void setMemory(String key, Object value) {
        if ( value == null ) this.memory.remove(key);
        else this.memory.put(key, value);
    }

    public final boolean hasMemory(String key) {
        return memory.containsKey(key) && memory.get(key) != null;
    }

}
