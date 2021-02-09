package com.gufli.kingdomcraft.api.events;

import com.gufli.kingdomcraft.api.domain.Rank;
import com.gufli.kingdomcraft.api.domain.User;

public class UserRankChangeEvent extends UserEvent {

    private final Rank prevRank;

    public UserRankChangeEvent(User user, Rank prevRank) {
        super(user);
        this.prevRank = prevRank;
    }

    public final Rank getPreviousRank() {
        return prevRank;
    }
}
