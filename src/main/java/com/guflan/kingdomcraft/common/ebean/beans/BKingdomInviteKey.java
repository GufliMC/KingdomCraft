package com.guflan.kingdomcraft.common.ebean.beans;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class BKingdomInviteKey implements Serializable {

    @Column(name = "player_id")
    String playerId;

    @Column(name = "kingdom_id")
    int kingdomId;

    @Override
    public int hashCode() {
        return playerId.hashCode() & kingdomId;
    }

    @Override
    public boolean equals(Object other) {
        if ( !(other instanceof BKingdomInviteKey) ) {
            return false;
        }
        return playerId.equals(((BKingdomInviteKey) other).playerId) && kingdomId == ((BKingdomInviteKey) other).kingdomId;
    }

}
