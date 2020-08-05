package com.igufguf.kingdomcraft.domain;

import io.ebean.Model;
import io.ebean.annotation.NotNull;
import io.ebean.annotation.WhenCreated;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.time.Instant;
import java.util.UUID;

@Entity
public class Player extends Model {

    @Id
    String id;

    @Column(nullable = false, unique = true)
    String name;

    @WhenCreated
    Instant createdAt;

    @ManyToOne
    Kingdom kingdom;

    @ManyToOne()
    Rank rank;

    public Player(UUID uuid, String name) {
        this.id = uuid.toString();
        this.name = name;
    }

    public UUID getId() {
        return UUID.fromString(id);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Kingdom getKingdom() {
        return kingdom;
    }

    public void setKingdom(Kingdom kingdom) {
        this.kingdom = kingdom;

        if ( kingdom == null ) {
            this.rank = null;
        } else if ( this.rank.getKingdom() != kingdom ) {
            this.rank = kingdom.getDefaultRank();
        }
    }

    public Rank getRank() {
        return rank;
    }

    public void setRank(Rank rank) {
        this.rank = rank;

        if ( rank == null ) {
            this.kingdom = null;
        } else {
            this.kingdom = rank.getKingdom();
        }
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
