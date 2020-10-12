package com.guflan.kingdomcraft.api.domain.models;

import java.util.Date;

public interface KingdomInvite {

    User getPlayer();

    User getSender();

    Kingdom getKingdom();

    Date getCreatedAt();

}
