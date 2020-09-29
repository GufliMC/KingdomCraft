package com.guflan.kingdomcraft.api.domain;

import java.util.UUID;

public interface Player {

    UUID getUniqueId();

    String getName();

    Rank getRank();

    Kingdom getKingdom();

    void setKingdom(Kingdom kingdom);

    void setRank(Rank rank);

    boolean hasInvite(Kingdom kingdom);

    void addInvite(Player sender);

}
