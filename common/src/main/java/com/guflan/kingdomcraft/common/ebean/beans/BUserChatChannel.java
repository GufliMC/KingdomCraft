package com.guflan.kingdomcraft.common.ebean.beans;

import com.guflan.kingdomcraft.api.domain.User;
import com.guflan.kingdomcraft.api.domain.UserChatChannel;
import io.ebean.Model;
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
public class BUserChatChannel extends Model implements UserChatChannel {

    @Id
    public long id;

    @ManyToOne
    @DbForeignKey(onDelete = ConstraintMode.CASCADE)
    public BUser user;

    public String channel;

    public boolean enabled = true;

    //

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
