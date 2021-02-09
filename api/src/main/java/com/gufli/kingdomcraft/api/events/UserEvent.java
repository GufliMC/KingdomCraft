package com.gufli.kingdomcraft.api.events;

import com.gufli.kingdomcraft.api.domain.User;

public class UserEvent implements Event {

    private final User user;

    public UserEvent(User user) {
        this.user = user;
    }

    public final User getUser() {
        return user;
    }

}
