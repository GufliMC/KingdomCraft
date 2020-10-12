package com.guflan.kingdomcraft.api.domain.models;

import java.util.UUID;

public interface User {

    UUID getUniqueId();

    String getName();

    Rank getRank();

    Kingdom getKingdom();

    void setKingdom(Kingdom kingdom);

    void setRank(Rank rank);

    boolean hasInvite(Kingdom kingdom);

    void addInvite(User sender);

}
