package com.igufguf.kingdomcraft.api.domain;

import java.util.List;
import java.util.UUID;

public interface Player extends Entity {

    UUID getUniqueId();

    String getName();

    Rank getRank();

    Kingdom getKingdom();

    void setKingdom(Kingdom kingdom);

    void setRank(Rank rank);

    List<KingdomInvite> getInvites();

    boolean isInvitedFor(Kingdom kingdom);

    void addInvite(KingdomInvite invite);

    boolean hasAdminMode();

    void setAdminMode(boolean adminMode);

}
