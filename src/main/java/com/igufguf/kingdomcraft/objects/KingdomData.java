package com.igufguf.kingdomcraft.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public abstract class KingdomData {

    protected final Map<String, Object> data = new HashMap<>();

    public final Object getData(String key) {
        return data.get(key);
    }

    public final int getInt(String key) {
        return (int) getData(key);
    }

    public final long getLong(String key) {
        return (long) getData(key);
    }

    public final String getString(String key) {
        return (String) getData(key);
    }

    public final boolean getBoolean(String key) {
        return (boolean) getData(key);
    }

    public final double getDouble(String key) {
        return (double) getData(key);
    }

    public final void setData(String key, Object value) {
        if ( value == null ) this.data.remove(key);
        else this.data.put(key, value);

        //only changed data will be saved so users can edit files while server is running
        if ( !hasInLocalList("changes", key) ) addInLocalList("changes", key);
    }

    public final boolean hasData(String key) {
        return data.containsKey(key) && data.get(key) != null;
    }

    public final Map<String, Object> getDataMap() {
        return data;
    }

    /** Easy adding / removing things from a list **/

    public final void addInList(String list, Object value) {
        if ( hasData(list) && getData(list) instanceof List) {
            List l = (List) getData(list);
            l.add(value);
        } else if ( !hasData(list) ){
            List<Object> objects = new ArrayList<>();
            objects.add(value);

            setData(list, objects);
        } else return;
        if ( !hasInLocalList("changes", list) ) addInLocalList("changes", list);
    }

    public final void delInList(String list, Object value) {
        if ( hasData(list) && getData(list) instanceof List) {
            List l = (List) getData(list);
            l.remove(value);

            if ( !hasInLocalList("changes", list) ) addInLocalList("changes", list);
        }
    }

    public final boolean hasInList(String list, Object value) {
        if ( hasData(list) && getData(list) instanceof List) {
            List l = (List) getData(list);
            return l.contains(value);
        }
        return false;
    }

    public final <T> List<T> getList(String key, Class<T> type) {
        return (List<T>) getData(key);
    }

    /** Local Data, this data will notbe queried by the plugin to save in a file **/

    private final HashMap<String, Object> localdata = new HashMap<>();

    public Object getLocalData(String key) {
        return localdata.get(key);
    }

    public final int getLocalInt(String key) {
        return (int) getLocalData(key);
    }

    public final long getLocalLong(String key) {
        return (long) getLocalData(key);
    }

    public final String getLocalString(String key) {
        return (String) getLocalData(key);
    }

    public final boolean getLocalBoolean(String key) {
        return (boolean) getLocalData(key);
    }

    public final double getLocalDouble(String key) {
        return (double) getLocalData(key);
    }

    public boolean hasLocalData(String key) {
        return localdata.containsKey(key) && localdata.get(key) != null;
    }

    public void setLocalData(String key, Object value) {
        if ( value == null ) this.localdata.remove(key);
        else this.localdata.put(key, value);
    }

    public final boolean hasInLocalList(String list, Object value) {
        if ( hasLocalData(list) && getLocalData(list) instanceof List) {
            List l = (List) getLocalData(list);
            return l.contains(value);
        }
        return false;
    }

    public final void addInLocalList(String list, Object value) {
        if ( hasLocalData(list) && getLocalData(list) instanceof List) {
            List l = (List) getLocalData(list);
            l.add(value);
        } else {
            List<Object> objects = new ArrayList<>();
            objects.add(value);

            setLocalData(list, objects);
        }
    }

    public final void delInLocalList(String list, Object value) {
        if ( hasLocalData(list) && getLocalData(list) instanceof List) {
            List l = (List) getLocalData(list);
            l.remove(value);
        }
    }

    public final <T> List<T> getLocalList(String key, Class<T> type) {
        return (List<T>) getData(key);
    }

    public final Map<String, Object> getLocalDataMap() {
        return localdata;
    }
}
