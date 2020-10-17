package com.guflan.kingdomcraft.bukkit.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.regex.Pattern;

public class LocationSerializer {

    public static String serialize(Location location) {
        if ( location == null || location.getWorld() == null ) {
            return "";
        }

        DecimalFormat df = new DecimalFormat("#.#", new DecimalFormatSymbols(Locale.ROOT));
        return location.getWorld().getName() + " , "
                + df.format(location.getX()) + " , "
                + df.format(location.getY()) + " , "
                + df.format(location.getZ()) + " , "
                + df.format(location.getYaw()) + " , "
                + df.format(location.getPitch());
    }

    public static Location deserialize(String str) {
        str = str.replace(" ",  "");
        if ( str.equals("") ) {
            return null;
        }

        String[] parts = str.split(Pattern.quote(","));
        if ( parts.length < 4  ) {
            return null;
        }

        if ( Bukkit.getWorld(parts[0]) == null ) {
            return null;
        }

        Location loc = new Location(
                Bukkit.getWorld(parts[0]),
                Double.parseDouble(parts[1]),
                Double.parseDouble(parts[2]),
                Double.parseDouble(parts[3]));

        if ( parts.length >= 5 ) {
            loc.setYaw(Float.parseFloat(parts[4]));
        }

        if ( parts.length == 6 ) {
            loc.setPitch(Float.parseFloat(parts[5]));
        }
        return loc;
    }

}
