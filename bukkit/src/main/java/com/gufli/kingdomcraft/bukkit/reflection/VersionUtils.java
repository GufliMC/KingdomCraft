package com.gufli.kingdomcraft.bukkit.reflection;

import org.bukkit.Bukkit;

import java.util.Arrays;
import java.util.regex.Pattern;

public class VersionUtils {

    public static String getVersion() {
        return Bukkit.getBukkitVersion().split("-")[0];
    }

    /**
     * @param v1 version number to compare with
     * @param v2 version number
     * @return the value 0 if v1 == v2; the value -1 if v1 < v2; the value 1 if v1 > v1
     */
    public static int compareVersions(String v1, String v2) {
        int[] va1 = parseVersion(v1);
        int[] va2 = parseVersion(v2);

        for (int i = 0; i < Math.min(va2.length, va1.length); i++) {
            if (va1[i] == va2[i]) {
                continue;
            }

            return Integer.compare(va1[i], va2[i]);
        }

        return 0;
    }

    private static int[] parseVersion(String version) {
        String[] va = version.split(Pattern.quote("."));
        String vl = va[va.length - 1];
        for (int i = 0; i < vl.length(); i++) {
            if ( !Character.isDigit(vl.charAt(i)) ) {
                va[va.length - 1] = vl.substring(0, i);
                break;
            }
        }
        return Arrays.stream(va)
                .filter(s -> !s.equals(""))
                .mapToInt(Integer::parseInt)
                .toArray();
    }

    public static boolean isMcLowerOrEqualTo(String version) {
        return compareVersions(getVersion(), version) <= 0;
    }

    public static boolean isMcLowerThan(String version) {
        return compareVersions(getVersion(), version) < 0;
    }

    public static boolean isMcGreaterThan(String version) {
        return compareVersions(getVersion(), version) > 0;
    }

    public static boolean isMcGreaterOrEqualTo(String version) {
        return compareVersions(getVersion(), version) >= 0;
    }


}
