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
import com.guflan.kingdomcraft.api.domain.User;
import io.ebean.Model;
import io.ebean.annotation.ConstraintMode;
import io.ebean.annotation.DbForeignKey;
import io.ebean.annotation.DbName;
import io.ebean.annotation.WhenCreated;

import javax.persistence.*;
import java.util.Date;

@DbName("kingdomcraft")
@Entity
@Table(name = "user_invites")
public class BKingdomInvite extends Model implements KingdomInvite {

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

    //

    public BKingdomInvite() {
        super("kingdomcraft");
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
    public Date getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean isValid() {
        return sender.getKingdom().equals(kingdom)
                && System.currentTimeMillis() - createdAt.getTime() <= 1000 * 60 * 60; // 1h
    }
}
