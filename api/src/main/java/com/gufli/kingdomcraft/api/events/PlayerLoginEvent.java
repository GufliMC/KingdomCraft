package com.gufli.kingdomcraft.api.events;

import com.gufli.kingdomcraft.api.entity.PlatformPlayer;

public class PlayerLoginEvent extends PlayerEvent {

    public PlayerLoginEvent(PlatformPlayer player) {
        super(player);
    }

}
