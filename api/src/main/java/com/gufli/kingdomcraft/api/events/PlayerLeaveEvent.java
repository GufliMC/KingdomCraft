package com.gufli.kingdomcraft.api.events;

import com.gufli.kingdomcraft.api.entity.PlatformPlayer;

public class PlayerLeaveEvent extends PlayerEvent {

    public PlayerLeaveEvent(PlatformPlayer player) {
        super(player);
    }

}
