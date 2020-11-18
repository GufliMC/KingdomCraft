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

import com.guflan.kingdomcraft.api.domain.*;
import io.ebean.DB;
import io.ebean.Model;
import io.ebean.annotation.*;
import io.ebean.annotation.ConstraintMode;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@DbName("kingdomcraft")
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

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    public List<BKingdomInvite> kingdomInvites;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    public List<BUserChatChannel> chatChannels;

    @WhenCreated
    public Date createdAt;

    @WhenModified
    public Date updatedAt;

    //

    public BUser() {
        super("kingdomcraft");
    }

    @Override
    public boolean delete() {

        return super.delete();
    }

    @Override
    public void save() {

        super.save();
    }

    public void update(UUID uuid, String name) {
        this.id = uuid.toString();
        this.name = name;
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
        if ( kingdom == this.kingdom ) {
            return;
        }

        if ( this.kingdom != null ) {
            this.kingdom.memberCount--;
        }
        if ( this.rank != null ) {
            this.rank.memberCount--;
        }

        if ( kingdom == null ) {
            this.kingdom = null;
            this.rank = null;
            return;
        }

        this.kingdom = (BKingdom) kingdom;
        this.kingdom.memberCount++;

        if ( kingdom.getDefaultRank() != null ) {
            this.rank = (BRank) kingdom.getDefaultRank();
            this.rank.memberCount++;
        } else {
            this.rank = null;
        }
    }

    @Override
    public void setRank(Rank rank) {
        if ( rank == this.rank ) {
            return;
        }

        if ( rank != null && !rank.getKingdom().equals(kingdom) ) {
            throw new IllegalArgumentException("The given rank does not belong to the user their kingdom.");
        }

        if ( this.rank != null ) {
            this.rank.memberCount--;
        }

        if ( rank == null ) {
            this.rank = null;
            return;
        }

        this.rank = (BRank) rank;
        this.rank.memberCount++;
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

    @Override
    public UserChatChannel addChatChannel(String channel) {
        BUserChatChannel ucc = (BUserChatChannel) getChatChannel(channel);
        if ( ucc != null ) {
            return ucc;
        }

        ucc = new BUserChatChannel();
        ucc.user = this;
        ucc.channel = channel;

        chatChannels.add(ucc);
        return ucc;
    }

    @Override
    public UserChatChannel getChatChannel(String channel) {
        return chatChannels.stream().filter(cs -> cs.channel.endsWith(channel)).findFirst().orElse(null);
    }

    @Override
    public Date getCreatedAt() {
        return createdAt;
    }

    @Override
    public Date getUpdatedAt() {
        return updatedAt;
    }
}
