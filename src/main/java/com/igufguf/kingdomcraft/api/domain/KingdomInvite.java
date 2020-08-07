package com.igufguf.kingdomcraft.api.domain;

import java.util.Date;

public interface KingdomInvite {

    Player getPlayer();

    Player getSender();

    Kingdom getKingdom();

    Date getCreatedAt();

}
