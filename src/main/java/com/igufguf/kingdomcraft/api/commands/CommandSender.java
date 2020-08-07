package com.igufguf.kingdomcraft.api.commands;

import com.igufguf.kingdomcraft.api.domain.Player;

public interface CommandSender {

    void sendMessage(String msg);

    boolean hasPermission(String permission);

    Player getPlayer();

    default boolean isConsole() {
        return getPlayer() == null;
    }

}
