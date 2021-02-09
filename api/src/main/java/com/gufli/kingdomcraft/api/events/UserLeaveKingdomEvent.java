package com.gufli.kingdomcraft.api.events;

import com.gufli.kingdomcraft.api.domain.Kingdom;
import com.gufli.kingdomcraft.api.domain.User;

public class UserLeaveKingdomEvent extends UserEvent {

    private final Kingdom kingdom;

    public UserLeaveKingdomEvent(User user, Kingdom kingdom) {
        super(user);
        this.kingdom = kingdom;
    }

    public final Kingdom getKingdom() {
        return kingdom;
    }
}
