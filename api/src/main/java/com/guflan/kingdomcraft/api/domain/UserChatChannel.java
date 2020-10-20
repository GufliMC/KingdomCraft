package com.guflan.kingdomcraft.api.domain;

public interface UserChatChannel extends Model {

    User getUser();

    String getChannelName();

    boolean isEnabled();

    void setEnabled(boolean enabled) ;

}
