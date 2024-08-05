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

import com.gufli.kingdomcraft.api.domain.Kingdom;
import com.gufli.kingdomcraft.api.domain.KingdomInvite;
import com.gufli.kingdomcraft.api.domain.User;
import io.ebean.annotation.ConstraintMode;
import io.ebean.annotation.DbForeignKey;
import io.ebean.annotation.WhenCreated;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "user_invites")
public class BKingdomInvite extends BaseModel implements KingdomInvite {

    @Id
    public int id;

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
    public Instant createdAt;

    //

    public BKingdomInvite() {
        super();
    }

    @Override
    public boolean delete() {
        user.kingdomInvites.remove(this);
        return super.delete();
    }

    @Override
    public void save() {
        super.save();
    }

    // interface

    @Override
    public User getPlayer() {
        return user;
    }

    @Override
    public User getSender() {
        return sender;
    }

    @Override
    public Kingdom getKingdom() {
        return kingdom;
    }

    @Override
    public Instant getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean isValid() {
        return sender.getKingdom().equals(kingdom)
                && Instant.now().minusSeconds(3600).isBefore(createdAt); // 1h
    }
}
