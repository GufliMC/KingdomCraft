package com.guflan.kingdomcraft.common.ebean.beans;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class RelationKey implements Serializable {

    @Column(name = "kingdom_id")
    int kingdomId;

    @Column(name = "target_kingdom_id")
    int targetKingdomId;

    @Override
    public int hashCode() {
        return kingdomId & targetKingdomId;
    }

    @Override
    public boolean equals(Object other) {
        if ( !(other instanceof RelationKey) ) {
            return false;
        }
        return kingdomId == ((RelationKey) other).kingdomId && targetKingdomId == ((RelationKey) other).targetKingdomId;
    }

}
