package com.guflan.kingdomcraft.common.ebean.beans;

import io.ebean.annotation.WhenCreated;
import io.ebean.annotation.WhenModified;

import javax.persistence.*;
import java.time.Instant;

@Entity
public class BRelation {

    @EmbeddedId
    BRelationKey id;

    @ManyToOne
    @MapsId("kingdomId")
    @JoinColumn(name = "kingdom_id")
    BKingdom kingdom;

    @ManyToOne
    @MapsId("targetKingdomId")
    @JoinColumn(name = "target_kingdom_id")
    BKingdom target;

    int relation;

    @WhenCreated
    Instant created_at;

    @WhenModified
    Instant updated_at;

    public BRelation(BKingdom kingdom, BKingdom target, int relation) {
        this.kingdom = kingdom;
        this.target = target;
        this.relation = relation;
    }

}
