package com.igufguf.kingdomcraft.objects;

import org.apache.commons.lang3.StringEscapeUtils;
import org.bukkit.ChatColor;

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
public class KingdomRank extends KingdomData {

    private final KingdomObject kingdom;
    private final String name;

    public KingdomRank(KingdomObject kingdom, String name) {
        this.kingdom = kingdom;
        this.name = name;
    }

    public KingdomObject getKingdom() {
        return kingdom;
    }

    public String getName() {
        return name;
    }

    public String getDisplay() {
        return hasData("display") ? ChatColor.translateAlternateColorCodes('&', StringEscapeUtils.unescapeJava((String) getData("display"))) : getName();
    }

    public Map<String, Boolean> getPermissions() {
        Map<String, Boolean> output = new HashMap<>();

        if ( hasData("permissions") && getData("permissions") instanceof List) {
            List<String> list = getList("permissions", String.class);

            for ( String perm : list ) {
                if ( perm.startsWith("-") ) {
                    output.put(perm.substring(1).trim(), false);
                } else {
                    output.put(perm.trim(), true);
                }
            }
        }

        return output;
    }

}
