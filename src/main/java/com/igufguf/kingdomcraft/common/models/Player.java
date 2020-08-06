package com.igufguf.kingdomcraft.common.models;

import java.util.UUID;

public class Player {

    UUID id;
    String name;

    Rank rank;

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Rank getRank() {
        return rank;
    }

    public Kingdom getKingdom() {
        return rank != null ? rank.kingdom : null;
    }

}
