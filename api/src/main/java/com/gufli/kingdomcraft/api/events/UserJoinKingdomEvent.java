package com.gufli.kingdomcraft.api.events;

import com.gufli.kingdomcraft.api.domain.User;

public class UserJoinKingdomEvent extends UserEvent {

    public UserJoinKingdomEvent(User user) {
        super(user);
    }

}
