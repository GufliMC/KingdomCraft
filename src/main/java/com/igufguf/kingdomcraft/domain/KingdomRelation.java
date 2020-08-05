package com.igufguf.kingdomcraft.domain;

import com.avaje.ebean.annotation.CreatedTimestamp;
import com.igufguf.kingdomcraft.models.Relation;
import io.ebean.Model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Table(
        uniqueConstraints=
        @UniqueConstraint(columnNames={"kingdom_id", "target_id"})
)
@Entity
public class KingdomRelation extends Model {

    @Column(nullable = false)
    @OneToOne
    Kingdom kingdom;

    @Column(nullable = false)
    @OneToOne
    Kingdom target;

    @Column(nullable = false)
    Relation relation;

    @CreatedTimestamp
    Date createdAt;

    public KingdomRelation(Kingdom kingdom, Kingdom target, Relation relation) {
        this.kingdom = kingdom;
        this.target = target;
        this.relation = relation;
    }

    public Kingdom getKingdom() {
        return kingdom;
    }

    public Kingdom getTarget() {
        return target;
    }

    public Relation getRelation() {
        return relation;
    }

}
