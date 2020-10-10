package com.guflan.kingdomcraft.common.ebean.beans;

import com.guflan.kingdomcraft.api.domain.Kingdom;
import com.guflan.kingdomcraft.api.domain.User;
import com.guflan.kingdomcraft.api.domain.Rank;
import io.ebean.Model;
import io.ebean.annotation.ConstraintMode;
import io.ebean.annotation.DbForeignKey;
import io.ebean.annotation.WhenCreated;
import io.ebean.annotation.WhenModified;

import javax.persistence.*;
import java.time.Instant;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
public class BUser extends Model implements User {

    @Id
    public String id;

    @Column(unique=true)
    public String name;

    @ManyToOne
    @DbForeignKey(onDelete = ConstraintMode.CASCADE)
    public BRank rank;

    @ManyToOne
    @DbForeignKey(onDelete = ConstraintMode.CASCADE)
    public BKingdom kingdom;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    public Set<BKingdomInvite> kingdomInvites;

    @WhenCreated
    public Date createdAt;

    @WhenModified
    public Date updatedAt;

    // interface

    @Override
    public UUID getUniqueId() {
        return UUID.fromString(id);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Rank getRank() {
        return rank;
    }

    @Override
    public Kingdom getKingdom() {
        return kingdom;
    }

    @Override
    public void setKingdom(Kingdom kingdom) {
        if ( kingdom == null ) {
            this.kingdom = null;
            this.rank = null;
        } else {
            this.kingdom = (BKingdom) kingdom;
            this.rank = (BRank) kingdom.getDefaultRank();
        }
    }

    @Override
    public void setRank(Rank rank) {
        if ( rank.getKingdom() != kingdom ) {
            throw new IllegalArgumentException("The given rank does not belong to the user their kingdom.");
        }
        this.rank = (BRank) rank;
    }

    @Override
    public boolean hasInvite(Kingdom kingdom) {
        for ( BKingdomInvite invite : kingdomInvites ) {
            if ( invite.kingdom == kingdom && invite.user.getKingdom() == kingdom ) {
                return true; // TODO time check
            }
        }
        return false;
    }

    @Override
    public void addInvite(User sender) {
        if ( sender.getKingdom() == null ) {
            return;
        }

        BKingdomInvite invite = new BKingdomInvite();
        invite.kingdom = (BKingdom) sender.getKingdom();
        invite.sender = (BUser) sender;
        invite.user = this;

        this.kingdomInvites.add(invite);
    }

}
