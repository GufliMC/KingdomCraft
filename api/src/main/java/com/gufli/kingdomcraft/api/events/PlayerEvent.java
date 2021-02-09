package com.gufli.kingdomcraft.api.events;

import com.gufli.kingdomcraft.api.entity.PlatformPlayer;

public class PlayerEvent implements Event {

    private final PlatformPlayer player;

    public PlayerEvent(PlatformPlayer player) {
        this.player = player;
    }

    public final PlatformPlayer getPlayer() {
        return player;
    }

}
