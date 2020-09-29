package com.guflan.kingdomcraft.api.domain;

public enum Relation {
    NEUTRAL, ENEMY, FRIENDLY;

    public static Relation fromId(int id) {
        return Relation.values()[id];
    }

    public int getId() {
        return this.ordinal();
    }
}
