package com.igufguf.kingdomcraft.objects;

import java.util.List;

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
