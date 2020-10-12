package com.guflan.kingdomcraft.api.domain.models;

public enum RelationType {
    NEUTRAL, ENEMY, ALLY, TRUCE;

    public static RelationType fromId(int id) {
        return RelationType.values()[id];
    }

    public int getId() {
        return this.ordinal();
    }
}
