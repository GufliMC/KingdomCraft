package com.igufguf.kingdomcraft.objects;

import java.util.List;

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

    private final String name;

    public KingdomRank(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String[] getPermissions() {
        if ( hasData("permissions") && getData("permissions") instanceof List) {
            List<String> list = ((List) getData("permissions"));
            return list.toArray(new String[list.size()]);
        }
        return new String[0];
    }

}
