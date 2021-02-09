package com.gufli.kingdomcraft.api.events;

import com.gufli.kingdomcraft.api.domain.Kingdom;

public class KingdomCreateEvent extends KingdomEvent {

    public KingdomCreateEvent(Kingdom kingdom) {
        super(kingdom);
    }

}
