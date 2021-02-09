package com.gufli.kingdomcraft.api.events;

import com.gufli.kingdomcraft.api.entity.PlatformPlayer;

public class PlayerLoadedEvent extends PlayerEvent {

    public PlayerLoadedEvent(PlatformPlayer player) {
        super(player);
    }

}
