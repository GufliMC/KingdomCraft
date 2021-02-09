package com.gufli.kingdomcraft.api.events;

import com.gufli.kingdomcraft.api.domain.Kingdom;

public class KingdomDeleteEvent extends KingdomEvent {

    public KingdomDeleteEvent(Kingdom kingdom) {
        super(kingdom);
    }

}
