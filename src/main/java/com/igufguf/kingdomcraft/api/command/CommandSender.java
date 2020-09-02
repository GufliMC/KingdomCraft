package com.igufguf.kingdomcraft.api.command;

import com.igufguf.kingdomcraft.api.domain.Entity;
import com.igufguf.kingdomcraft.api.domain.Player;

public interface CommandSender extends Entity {

    Player getPlayer();

    default boolean isConsole() {
        return getPlayer() == null;
    }

}
