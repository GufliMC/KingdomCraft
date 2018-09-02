package com.igufguf.kingdomcraft.api.events;

import com.igufguf.kingdomcraft.api.models.kingdom.Kingdom;

/**
 * Created by Joris on 19/08/2018 in project KingdomCraft.
 */
public class KingdomLoadEvent extends KingdomEvent {

    private Kingdom kingdom;

    public KingdomLoadEvent(Kingdom kingdom) {
        this.kingdom = kingdom;
    }

    public Kingdom getKingdom() {
        return kingdom;
    }

}
