package com.guflan.kingdomcraft.bukkit.util;

import com.guflan.kingdomcraft.api.entity.PlatformLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class LocationConverter {

    public static Location convert(PlatformLocation loc) {
        if ( Bukkit.getWorld(loc.getWorldName()) == null ) {
            return null;
        }

        return new Location(Bukkit.getWorld(loc.getWorldName()),
                loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
    }

    public static PlatformLocation convert(Location loc) {
        return new PlatformLocation(loc.getWorld().getName(),
                loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
    }

}
