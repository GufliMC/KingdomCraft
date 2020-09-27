package com.guflan.kingdomcraft.common.ebean.beans;

import io.ebean.annotation.WhenCreated;
import io.ebean.annotation.WhenModified;

import javax.persistence.*;
import java.time.Instant;

@Entity
public class Relation {

    @EmbeddedId
    RelationKey id;

    @ManyToOne
    @MapsId("kingdomId")
    @JoinColumn(name = "kingdom_id")
    Kingdom kingdom;

    @ManyToOne
    @MapsId("targetKingdomId")
    @JoinColumn(name = "target_kingdom_id")
    Kingdom target;

    int relation;

    @WhenCreated
    Instant created_at;

    @WhenModified
    Instant updated_at;

}
