package com.guflan.kingdomcraft.api.domain;

import java.util.Date;

public interface KingdomInvite {

    User getPlayer();

    User getSender();

    Kingdom getKingdom();

    Date getCreatedAt();

}
