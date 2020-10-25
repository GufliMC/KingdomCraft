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

package com.guflan.kingdomcraft.api.entity;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.regex.Pattern;

public class PlatformLocation {

    String worldName;

    double x;
    double y;
    double z;

    float yaw;
    float pitch;

    public PlatformLocation(String worldName, double x, double y, double z) {
        this.worldName = worldName;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public PlatformLocation(String worldName, double x, double y, double z, float yaw, float pitch) {
        this(worldName, x, y, z);
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public String getWorldName() {
        return worldName;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public double distanceTo(PlatformLocation l) {
        return Math.sqrt(Math.pow(x - l.x, 2) + Math.pow(y - l.y, 2) + Math.pow(z - l.z, 2));

    }

    public String serialize() {
        DecimalFormat df = new DecimalFormat("#.#", new DecimalFormatSymbols(Locale.ROOT));
        return worldName + " , "
                + df.format(x) + " , "
                + df.format(y) + " , "
                + df.format(z) + " , "
                + df.format(yaw) + " , "
                + df.format(pitch);
    }

    public static PlatformLocation deserialize(String str) {
        if ( str == null ) {
            return null;
        }

        str = str.replace(" ",  "");
        if ( str.equals("") ) {
            return null;
        }

        String[] parts = str.split(Pattern.quote(","));
        if ( parts.length < 4  ) {
            return null;
        }

        PlatformLocation loc = new PlatformLocation(
                parts[0],
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
