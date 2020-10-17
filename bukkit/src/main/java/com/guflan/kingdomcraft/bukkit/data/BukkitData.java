package com.guflan.kingdomcraft.bukkit.data;

import com.guflan.kingdomcraft.api.domain.Kingdom;
import com.guflan.kingdomcraft.bukkit.KingdomCraftBukkitPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.regex.Pattern;

public class BukkitData {

    private final KingdomCraftBukkitPlugin plugin;
    private final File dataFile;

    private JSONObject data;

    public BukkitData(KingdomCraftBukkitPlugin plugin) {
        this.plugin = plugin;

        dataFile = new File(plugin.getDataFolder(), ".data");
        if ( !dataFile.exists() ) {
            data = new JSONObject();
            return;
        }

        try (FileReader reader = new FileReader(dataFile) ) {
            data = (JSONObject) new JSONParser().parse(reader);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    public Location getSpawn(Kingdom kingdom) {
        String key = "kingdoms." + kingdom.getName() + ".spawn";
        Object o = get("kingdoms." + kingdom.getName() + ".spawn");
        if ( o == null ) {
            return null;
        }
        Map m = (Map) o;
        return new Location(
                Bukkit.getWorld(String.valueOf(m.get("world"))),
                Double.parseDouble(String.valueOf(m.get("x"))),
                Double.parseDouble(String.valueOf(m.get("y"))),
                Double.parseDouble(String.valueOf(m.get("z"))),
                Float.parseFloat(String.valueOf(m.get("pitch"))),
                Float.parseFloat(String.valueOf(m.get("yaw")))
        );
    }

    public void setSpawn(Kingdom kingdom, Location location) {
        String key = "kingdoms." + kingdom.getName() + ".spawn";
        JSONObject obj = new JSONObject();
        obj.put("world", location.getWorld().getName());
        obj.put("x", location.getX());
        obj.put("y", location.getY());
        obj.put("z", location.getZ());
        obj.put("pitch", location.getPitch());
        obj.put("yaw", location.getYaw());
        set(key, obj);
    }

    private Object find(String key, Map obj) {
        if ( key.contains(".") ) {
            String p1 = key.split(Pattern.quote("."))[0];
            if ( !obj.containsKey(p1) ) {
                return null;
            }
            Object result = obj.get(p1);
            if ( !(result instanceof Map) ) {
                return null;
            }
            return find(key.substring(p1.length() + 1), (Map) result);
        }
        return obj.get(key);
    }

    private Object get(String key) {
        return find(key, data);
    }

    private <T> T get(String key, Class<T> clazz) {
        return (T) find(key, data);
    }

    private Object has(String key) {
        return find(key, data) != null;
    }

    private void set(String key, Object value) {
        if ( !key.contains(".") ) {
            data.put(key, value);
            return;
        }

        String[] path = key.split(Pattern.quote("."));
        Map data = this.data;

        for ( int i = 0; i < path.length - 1; i++ ) {
            Object obj = find(path[i], data);
            if ( obj instanceof Map ) {
                data = (Map) obj;
            } else if ( obj == null ) {
                data.put(path[i], data = new JSONObject());
            } else {
                return;
            }
        }

        data.put(path[path.length - 1], value);
    }

    public void save() {
        plugin.getScheduler().async().execute(() -> {
            if ( !dataFile.exists() ) {
                try {
                    dataFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            try (FileWriter writer = new FileWriter(dataFile) ) {
                writer.write(data.toJSONString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

}
