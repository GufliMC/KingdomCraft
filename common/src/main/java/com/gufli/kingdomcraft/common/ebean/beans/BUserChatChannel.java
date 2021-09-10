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

import com.gufli.kingdomcraft.api.domain.User;
import com.gufli.kingdomcraft.api.domain.UserChatChannel;
import io.ebean.annotation.ConstraintMode;
import io.ebean.annotation.DbForeignKey;
import io.ebean.annotation.Index;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Index(unique = true, columnNames = {"user_id", "channel"})
@Table(name = "user_chatchannels")
public class BUserChatChannel extends BaseModel implements UserChatChannel {

    @Id
    public int id;

    @ManyToOne
    @DbForeignKey(onDelete = ConstraintMode.CASCADE, onUpdate = ConstraintMode.CASCADE)
    public BUser user;

    public String channel;

    public boolean enabled = true;

    //

    public BUserChatChannel() {
        super();
    }

    @Override
    public boolean delete() {
        user.chatChannels.remove(this);
        return super.delete();
    }

    @Override
    public void save() {
        super.save();
    }

    // interface


    @Override
    public User getUser() {
        return user;
    }

    @Override
    public String getChannelName() {
        return channel;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
