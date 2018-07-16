package com.igufguf.kingdomcraft.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        this.data.put(key, value);
        if ( !hasInLocalList("changes", key) ) addInLocalList("changes", key); //only changed data will be saved so users can edit files while server is running
    }

    public final boolean hasData(String key) {
        return data.containsKey(key);
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
        return localdata.containsKey(key);
    }

    public void setLocalData(String key, Object value) {
        this.localdata.put(key, value);
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

    public final Map<String, Object> getLocalDataMap() {
        return localdata;
    }
}
