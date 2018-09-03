package com.igufguf.kingdomcraft.utils;

import org.apache.commons.lang3.StringEscapeUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.MemorySection;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by Joris on 15/08/2018 in project KingdomCraft.
 */
public class KingdomUtils {

    public static String strFromLocation(Location loc) {
        if ( loc == null || loc.getWorld() == null ) return null;
        return loc.getWorld().getName() + " , " + loc.getX() + " , " + loc.getY() + " , " + loc.getZ() + " , " + loc.getYaw() + " , " + loc.getPitch();
    }

    public static Location locFromString(String s) {
        s = s.replace(" ",  "");
        String[] split = s.split(Pattern.quote(","));

        if ( split.length < 4 && Bukkit.getWorld(split[0]) == null ) return null;

        Location loc = new Location(Bukkit.getWorld(split[0]), Double.valueOf(split[1]), Double.valueOf(split[2]), Double.valueOf(split[3]));
        if ( split.length == 6 ) {
            loc.setYaw(Float.valueOf(split[4]));
            loc.setPitch(Float.valueOf(split[5]));
        }

        return loc;
    }

    public static String formatString(String s) {
        return ChatColor.translateAlternateColorCodes('&', StringEscapeUtils.unescapeJava(s));
    }

    public static Map mapFromConfiguration(Object obj) {
        if ( obj instanceof Map ) {
            return (Map) obj;
        } else if ( obj instanceof MemorySection) {
            return ((MemorySection) obj).getValues(false);
        }
        throw new ClassCastException(obj.getClass().getName() + " cannot be cast to " + Map.class.getName());
    }

}
