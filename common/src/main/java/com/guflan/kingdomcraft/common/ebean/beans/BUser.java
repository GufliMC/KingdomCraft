/*
 * This file is part of KingdomCraft.
 *
 * KingdomCraft is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * KingdomCraft is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with KingdomCraft. If not, see <https://www.gnu.org/licenses/>.
 */

package com.guflan.kingdomcraft.common.ebean.beans;

import com.guflan.kingdomcraft.api.domain.Kingdom;
import com.guflan.kingdomcraft.api.domain.KingdomInvite;
import com.guflan.kingdomcraft.api.domain.Rank;
import com.guflan.kingdomcraft.api.domain.User;
import io.ebean.DB;
import io.ebean.Model;
import io.ebean.annotation.ConstraintMode;
import io.ebean.annotation.DbForeignKey;
import io.ebean.annotation.WhenCreated;
import io.ebean.annotation.WhenModified;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
public class BUser extends Model implements User {

    @Id
    public String id;

    @Column(unique=true)
    public String name;

    @ManyToOne
    @DbForeignKey(onDelete = ConstraintMode.SET_NULL)
    public BRank rank;

    @ManyToOne
    @DbForeignKey(onDelete = ConstraintMode.SET_NULL)
    public BKingdom kingdom;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    public List<BKingdomInvite> kingdomInvites;

    @WhenCreated
    public Date createdAt;

    @WhenModified
    public Date updatedAt;

    //

    @Override
    public boolean delete() {

        return super.delete();
    }

    @Override
    public void save() {

        super.save();
    }

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
            return;
        }

        if ( this.kingdom == null || !this.kingdom.equals(kingdom) ) {
            this.rank = (BRank) kingdom.getDefaultRank();
        }

        this.kingdom = (BKingdom) kingdom;
    }

    @Override
    public void setRank(Rank rank) {
        if ( !rank.getKingdom().equals(kingdom) ) {
            throw new IllegalArgumentException("The given rank does not belong to the user their kingdom.");
        }
        this.rank = (BRank) rank;
    }

    @Override
    public List<KingdomInvite> getInvites() {
        return new ArrayList<>(kingdomInvites);
    }

    @Override
    public KingdomInvite getInvite(Kingdom kingdom) {
        return kingdomInvites.stream().filter(ki -> ki.kingdom == kingdom).findFirst().orElse(null);
    }

    @Override
    public KingdomInvite addInvite(User sender) {
        if ( sender.getKingdom() == null ) {
            throw new IllegalArgumentException("Cannot add an invite from a player without a kingdom.");
        }

        BKingdomInvite invite = new BKingdomInvite();
        invite.kingdom = (BKingdom) sender.getKingdom();
        invite.sender = (BUser) sender;
        invite.user = this;

        this.kingdomInvites.add(invite);
        return invite;
    }

    @Override
    public void clearInvites() {
        DB.deleteAll(kingdomInvites);
        kingdomInvites.clear();
    }
}
