package com.guflan.kingdomcraft.common.ebean.beans;

import io.ebean.annotation.WhenCreated;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "user_invites")
public class BKingdomInvite {

    @Id
    public long id;

    @ManyToOne
    public BUser user;

    @ManyToOne
    public BKingdom kingdom;

    @ManyToOne
    public BUser sender;

    @WhenCreated
    public Instant createdAt;

}
