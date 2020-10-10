package com.guflan.kingdomcraft.common.ebean.beans;

import io.ebean.annotation.ConstraintMode;
import io.ebean.annotation.DbForeignKey;
import io.ebean.annotation.WhenCreated;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "user_invites")
public class BKingdomInvite {

    @Id
    public long id;

    @ManyToOne
    @DbForeignKey(onDelete = ConstraintMode.CASCADE)
    public BUser user;

    @ManyToOne
    @DbForeignKey(onDelete = ConstraintMode.CASCADE)
    public BKingdom kingdom;

    @ManyToOne
    @DbForeignKey(onDelete = ConstraintMode.CASCADE)
    public BUser sender;

    @WhenCreated
    public Date createdAt;

}
