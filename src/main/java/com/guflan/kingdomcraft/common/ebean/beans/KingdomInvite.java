package com.guflan.kingdomcraft.common.ebean.beans;

import io.ebean.annotation.WhenCreated;
import io.ebean.annotation.WhenModified;

import javax.persistence.*;
import java.time.Instant;

@Entity
public class KingdomInvite {

    @EmbeddedId
    KingdomInviteKey id;

    @ManyToOne
    @MapsId("playerId")
    @JoinColumn(name = "player_id")
    Player player;

    @ManyToOne
    @MapsId("kingdomId")
    @JoinColumn(name = "kingdom_id")
    Kingdom kingdom;

    @ManyToOne
    Player sender;

    @WhenCreated
    Instant created_at;

    @WhenModified
    Instant updated_at;

}
