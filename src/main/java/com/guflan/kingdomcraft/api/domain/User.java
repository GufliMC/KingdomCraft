package com.guflan.kingdomcraft.api.domain;

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
