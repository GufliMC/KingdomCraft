package com.igufguf.kingdomcraft.api.models.storage;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Joris on 15/08/2018 in project KingdomCraft.
 */
public class Storable extends MemoryHolder {

    private final Map<String, Object> data = new HashMap<>();

    // getters

    public final Object getData(String key) {
        return data.get(key);
    }

    public final <T> T getData(String key, Class<T> type) {
        return (T) data.get(key);
    }

    public final int getInt(String key) {
        return (int) getData(key);
    }

    public final long getLong(String key) {
        return (long) getData(key);
    }

    public final String getString(String key) {
        return getData(key) + "";
    }

    public final boolean getBoolean(String key) {
        return (boolean) getData(key);
    }

    public final double getDouble(String key) {
        return (double) getData(key);
    }

    public final boolean hasData(String key) {
        return data.containsKey(key) && data.get(key) != null;
    }

    public final <T> List<T> getList(String key, Class<T> type) {
        return (List<T>) getData(key);
    }

    public final <T, V> Map<T, V> getMap(String key, Class<T> keyType, Class<V> valueType) {
        return (Map<T, V>) getData(key);
    }

    // setters

    public final void setData(String key, Object value) {
        if ( value == null ) this.data.remove(key);
        else this.data.put(key, value);

        setChanged(key);
    }

    // internal

    private void setChanged(String key) {
        if ( !hasMemory("changes") ) {
            setMemory("changes", new ArrayList<String>());
        }

        List<String> changes = getMemoryList("changes", String.class);
        if ( changes.contains(key) ) return;

        changes.add(key);
    }

    // save

    public void save(ConfigurationSection data) {
        if ( !hasMemory("changes") ) return;

        // check for removed objects
        List<String> changes = getMemoryList("changes", String.class);
        for ( String key : changes ) {
            if ( !hasData(key) ) data.set(key, null);
        }

        // save all objects that aren't removed
        for ( String key : this.data.keySet() ) {
            data.set(key, this.data.get(key));
        }
    }

    // load

    public void load(ConfigurationSection data) {
        for ( String key : data.getKeys(false) ) {

            Object value = data.get(key);
            if (value instanceof MemorySection) {
                setData(key, ((MemorySection) value).getValues(false));
            } else {
                setData(key, value);
            }
        }
    }

}
