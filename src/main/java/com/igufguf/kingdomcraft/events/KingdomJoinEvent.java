package com.igufguf.kingdomcraft.events;

import com.igufguf.kingdomcraft.objects.KingdomUser;

public class KingdomJoinEvent extends KingdomEvent {

    private KingdomUser user;

    public KingdomJoinEvent(KingdomUser user) {
        this.user = user;
    }

    public KingdomUser getUser() {
        return user;
    }
}
