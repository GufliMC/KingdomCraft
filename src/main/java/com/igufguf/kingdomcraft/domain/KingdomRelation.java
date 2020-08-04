package com.igufguf.kingdomcraft.domain;

import com.igufguf.kingdomcraft.models.Relation;
import io.ebean.Model;
import io.ebean.annotation.WhenCreated;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import java.time.Instant;

@Entity
public class KingdomRelation extends Model {

    @OneToOne
    Kingdom kingdom;

    @OneToOne
    Kingdom target;

    Relation relation;

    @WhenCreated
    Instant createdAt;

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
