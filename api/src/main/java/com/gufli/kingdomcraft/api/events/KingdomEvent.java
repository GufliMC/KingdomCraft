package com.gufli.kingdomcraft.api.events;

import com.gufli.kingdomcraft.api.domain.Kingdom;

public class KingdomEvent implements Event  {

    private final Kingdom kingdom;

    public KingdomEvent(Kingdom kingdom) {
        this.kingdom = kingdom;
    }

    public final Kingdom getKingdom() {
        return kingdom;
    }
}
