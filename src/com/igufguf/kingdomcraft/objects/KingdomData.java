package com.igufguf.kingdomcraft.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class KingdomData {

    protected final HashMap<String, Object> data = new HashMap<>();

    public final Object getData(String key) {
        return data.get(key);
    }

    public final void setData(String key, Object value) {
        this.data.put(key, value);
        if ( !hasInLocalList("changes", key) ) addInLocalList("changes", key); //only changed data will be saved so users can edit files while server is running
    }

    public final boolean hasData(String key) {
        return data.containsKey(key);
    }


    /** Easy adding / removing things from a list **/

    public final void addInList(String list, Object value) {
        if ( hasData(list) && getData(list) instanceof List) {
            List l = (List) getData(list);
            l.add(value);
        } else {
            List<Object> objects = new ArrayList<>();
            objects.add(value);

            setData(list, objects);
        }
    }

    public final void delInList(String list, Object value) {
        if ( hasData(list) && getData(list) instanceof List) {
            List l = (List) getData(list);
            l.remove(value);
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
}
