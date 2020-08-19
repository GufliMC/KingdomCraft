package com.igufguf.kingdomcraft.api.domain;

import java.util.List;
import java.util.UUID;

public interface Player {

    UUID getUniqueId();

    String getName();

    Rank getRank();

    Kingdom getKingdom();

    void setKingdom(Kingdom kingdom);

    List<KingdomInvite> getInvites();

    boolean isInvitedFor(Kingdom kingdom);

    void addInvite(KingdomInvite invite);

}
