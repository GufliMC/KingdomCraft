package com.igufguf.kingdomcraft.common.models;

import java.util.Comparator;

public enum Relation implements Comparator<Relation> {
    NEUTRAL, ENEMY, FRIENDLY;

    @Override
    public int compare(Relation o1, Relation o2) {
        return o1.compareTo(o2);
    }
}
