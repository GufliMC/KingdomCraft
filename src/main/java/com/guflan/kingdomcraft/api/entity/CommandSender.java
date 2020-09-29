package com.guflan.kingdomcraft.api.entity;

import com.guflan.kingdomcraft.api.domain.Player;

public interface CommandSender {

    Player getPlayer();

    default boolean isConsole() {
        return getPlayer() == null;
    }

    boolean hasPermission(String permission);

    void sendMessage(String msg);

}
