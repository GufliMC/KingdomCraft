package com.igufguf.kingdomcraft.api.domain;

import java.util.List;
import java.util.UUID;

public interface Player {

    public UUID getUniqueId();

    public String getName();

    public Rank getRank();

    public Kingdom getKingdom();

    void setKingdom(Kingdom kingdom);

    public List<KingdomInvite> getInvites();

    public boolean isInvitedFor(Kingdom kingdom);

    public void addInvite(KingdomInvite invite);

}
