package com.igufguf.kingdomcraft.api.models;

import java.util.UUID;

public interface Player {

    public UUID getUniqueId();

    public String getName();

    public Rank getRank();

    public Kingdom getKingdom();

    void setKingdom(Kingdom kingdom);

}
