package com.guflan.kingdomcraft.api.command;

import com.guflan.kingdomcraft.api.domain.Entity;
import com.guflan.kingdomcraft.api.domain.Player;

public interface CommandSender extends Entity {

    Player getPlayer();

    default boolean isConsole() {
        return getPlayer() == null;
    }

}
