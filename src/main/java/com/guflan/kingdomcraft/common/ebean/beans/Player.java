package com.guflan.kingdomcraft.common.ebean.beans;

import io.ebean.Model;
import io.ebean.annotation.WhenCreated;
import io.ebean.annotation.WhenModified;

import javax.persistence.*;
import java.time.Instant;
import java.util.Set;

@Entity
@Table(name = "players")
public class Player extends Model {

    @Id
    String id;

    @WhenCreated
    Instant created_at;

    @WhenModified
    Instant updated_at;

    @Column(unique=true)
    String name;

    Rank rank;

    @OneToMany(mappedBy = "player")
    Set<KingdomInvite> kingdomInvites;

    public Player(String id, String name) {
        this.id = id;
        this.name = name;
    }

}
