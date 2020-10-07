package com.guflan.kingdomcraft.common.ebean.beans;

import io.ebean.annotation.WhenCreated;
import io.ebean.annotation.WhenModified;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "kingdom_relations")
public class BRelation {

    @Id
    public long id;

    @ManyToOne
    public BKingdom kingdom;

    @ManyToOne
    public BKingdom otherKingdom;

    public int relation;

    @WhenCreated
    public Instant createdAt;

    @WhenModified
    public Instant updatedAt;

}
