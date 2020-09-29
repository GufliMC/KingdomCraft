package com.guflan.kingdomcraft.common.ebean.beans;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class BRelationKey implements Serializable {

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
        if ( !(other instanceof BRelationKey) ) {
            return false;
        }
        return kingdomId == ((BRelationKey) other).kingdomId && targetKingdomId == ((BRelationKey) other).targetKingdomId;
    }

}
