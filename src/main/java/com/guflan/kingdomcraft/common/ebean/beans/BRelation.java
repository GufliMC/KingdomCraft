package com.guflan.kingdomcraft.common.ebean.beans;

import io.ebean.annotation.ConstraintMode;
import io.ebean.annotation.DbForeignKey;
import io.ebean.annotation.WhenCreated;
import io.ebean.annotation.WhenModified;

import javax.persistence.*;
import java.time.Instant;
import java.util.Date;

@Entity
@Table(name = "kingdom_relations")
public class BRelation {

    @Id
    public long id;

    @ManyToOne
    @DbForeignKey(onDelete = ConstraintMode.CASCADE)
    public BKingdom kingdom;

    @ManyToOne
    @DbForeignKey(onDelete = ConstraintMode.CASCADE)
    public BKingdom otherKingdom;

    public int relation;

    @WhenCreated
    public Date createdAt;

    @WhenModified
    public Date updatedAt;

}
