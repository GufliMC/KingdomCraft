package com.igufguf.kingdomcraft.domain;

import io.ebean.Model;
import io.ebean.annotation.WhenCreated;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import java.util.Date;

@Entity
public class KingdomInvite extends Model {

    @Column(nullable = false)
    @OneToOne
    Kingdom kingdom;

    @Column(nullable = false)
    @OneToOne
    Player sender;

    @Column(nullable = false)
    @OneToOne
    Player target;

    @WhenCreated
    Date createdAt;

    public KingdomInvite(Kingdom kingdom, Player sender, Player target) {
        this.kingdom = kingdom;
        this.sender = sender;
        this.target = target;
    }

    public Kingdom getKingdom() {
        return kingdom;
    }

    public Player getSender() {
        return sender;
    }

    public Player getTarget() {
        return target;
    }

}
