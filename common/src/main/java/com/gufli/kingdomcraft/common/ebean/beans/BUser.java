/*
 * This file is part of KingdomCraft.
 *
 * KingdomCraft is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * KingdomCraft is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with KingdomCraft. If not, see <https://www.gnu.org/licenses/>.
 */

package com.gufli.kingdomcraft.common.ebean.beans;

import com.gufli.kingdomcraft.api.KingdomCraftProvider;
import com.gufli.kingdomcraft.api.domain.*;
import com.gufli.kingdomcraft.api.events.UserJoinKingdomEvent;
import com.gufli.kingdomcraft.api.events.UserLeaveKingdomEvent;
import com.gufli.kingdomcraft.api.events.UserRankChangeEvent;
import io.ebean.annotation.ConstraintMode;
import io.ebean.annotation.*;

import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
public class BUser extends BaseModel implements User {

    @Id
    public UUID id;

    @Column(unique = true)
    public String name;

    @ManyToOne
    @DbForeignKey(onDelete = ConstraintMode.SET_NULL)
    public BRank rank;

    @ManyToOne
    @DbForeignKey(onDelete = ConstraintMode.SET_NULL)
    public BKingdom kingdom;

    public Instant joinedKingdomAt;

    public Instant lastOnlineAt;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    public List<BKingdomInvite> kingdomInvites;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    public List<BUserChatChannel> chatChannels;

    @DbDefault(value = "false")
    public boolean adminModeEnabled;

    @DbDefault(value = "false")
    public boolean socialSpyEnabled;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    public List<BUserAttribute> attributes;

    @WhenCreated
    public Instant createdAt;

    @WhenModified
    public Instant updatedAt;

    //

    public BUser() {
        super();
    }

    @Override
    public boolean delete() {
        return super.delete();
    }

    @Override
    public void save() {
        super.save();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUUID(UUID uuid) {
        this.id = uuid;
    }

    // interface

    @Override
    public UUID getUniqueId() {
        return id;
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
        if (kingdom == this.kingdom) {
            return;
        }

        if (this.kingdom != null) {
            this.kingdom.memberCount--;
        }
        if (this.rank != null) {
            this.rank.memberCount--;
        }

        Kingdom oldKingdom = this.kingdom;

        if (kingdom == null) {
            this.kingdom = null;
            this.rank = null;
            KingdomCraftProvider.get().getEventManager().dispatch(new UserLeaveKingdomEvent(this, oldKingdom));
            return;
        }

        this.kingdom = (BKingdom) kingdom;
        this.kingdom.memberCount++;
        this.joinedKingdomAt = Instant.now();

        if (kingdom.getDefaultRank() != null) {
            this.rank = (BRank) kingdom.getDefaultRank();
            this.rank.memberCount++;
        } else {
            this.rank = null;
        }

        if (oldKingdom != null) {
            KingdomCraftProvider.get().getEventManager().dispatch(new UserLeaveKingdomEvent(this, oldKingdom));
        }

        KingdomCraftProvider.get().getEventManager().dispatch(new UserJoinKingdomEvent(this));
    }

    @Override
    public void setRank(Rank rank) {
        if (rank == this.rank) {
            return;
        }

        if (rank != null && !rank.getKingdom().equals(kingdom)) {
            throw new IllegalArgumentException("The given rank does not belong to the user their kingdom.");
        }

        if (this.rank != null) {
            this.rank.memberCount--;
        }

        Rank oldRank = this.rank;

        if (rank == null) {
            this.rank = null;
        } else {
            this.rank = (BRank) rank;
            this.rank.memberCount++;
        }

        KingdomCraftProvider.get().getEventManager().dispatch(new UserRankChangeEvent(this, oldRank));
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
        if (sender.getKingdom() == null) {
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
        db().deleteAll(kingdomInvites);
        kingdomInvites.clear();
    }

    @Override
    public UserChatChannel addChatChannel(String channel) {
        BUserChatChannel ucc = (BUserChatChannel) getChatChannel(channel);
        if (ucc != null) {
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
    public boolean isAdminModeEnabled() {
        return adminModeEnabled;
    }

    @Override
    public void setAdminModeEnabled(boolean adminMode) {
        this.adminModeEnabled = adminMode;
    }

    @Override
    public boolean isSocialSpyEnabled() {
        return socialSpyEnabled;
    }

    @Override
    public void setSocialSpyEnabled(boolean socialSpy) {
        this.socialSpyEnabled = socialSpy;
    }

    public Instant getJoinedKingdomAt() {
        return joinedKingdomAt;
    }

    public Instant getLastOnlineAt() {
        return lastOnlineAt;
    }

    public void setLastOnlineAt(Instant lastOnlineAt) {
        this.lastOnlineAt = lastOnlineAt;
    }

    @Override
    public Instant getCreatedAt() {
        return createdAt;
    }

    @Override
    public Instant getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public Attribute getAttribute(String name) {
        return attributes.stream().filter(p -> p.getName().equals(name)).findFirst().orElse(null);
    }

    @Override
    public Attribute createAttribute(String name) {
        return attributes.stream().filter(p -> p.getName().equals(name)).findFirst().orElseGet(() -> {
            BUserAttribute attribute = new BUserAttribute();
            attribute.user = this;
            attribute.name = name;

            attributes.add(attribute);
            return attribute;
        });
    }
}
