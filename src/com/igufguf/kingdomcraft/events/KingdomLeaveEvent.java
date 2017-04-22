package com.igufguf.kingdomcraft.events;

import com.igufguf.kingdomcraft.objects.KingdomObject;
import com.igufguf.kingdomcraft.objects.KingdomRank;
import com.igufguf.kingdomcraft.objects.KingdomUser;

public class KingdomLeaveEvent extends KingdomEvent {

    private KingdomObject oldkingdom;
    private KingdomRank oldRank;
    private KingdomUser user;

    public KingdomLeaveEvent(KingdomUser user, KingdomObject oldKingdom, KingdomRank oldRank) {
        this.user = user;
        this.oldkingdom = oldKingdom;
        this.oldRank = oldRank;
    }

    public KingdomUser getUser() {
        return user;
    }

    public KingdomRank getOldRank() {
        return oldRank;
    }

    public KingdomObject getOldkingdom() {
        return oldkingdom;
    }
}
