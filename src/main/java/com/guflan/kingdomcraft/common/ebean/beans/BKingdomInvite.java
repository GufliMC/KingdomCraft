package com.guflan.kingdomcraft.common.ebean.beans;

import io.ebean.annotation.WhenCreated;
import io.ebean.annotation.WhenModified;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "kingdom_invites")
public class BKingdomInvite {

    @EmbeddedId
    BKingdomInviteKey id;

    @ManyToOne
    @MapsId("playerId")
    @JoinColumn(name = "player_id")
    BPlayer player;

    @ManyToOne
    @MapsId("kingdomId")
    @JoinColumn(name = "kingdom_id")
    BKingdom kingdom;

    @ManyToOne
    BPlayer sender;

    @WhenCreated
    Instant created_at;

    @WhenModified
    Instant updated_at;

    public BKingdomInvite(BPlayer player, BPlayer sender, BKingdom kingdom) {
        this.player = player;
        this.sender = sender;
        this.kingdom = kingdom;
    }

}
