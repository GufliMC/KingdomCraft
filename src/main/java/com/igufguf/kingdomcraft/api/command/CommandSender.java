package com.igufguf.kingdomcraft.api.command;

import com.igufguf.kingdomcraft.api.domain.Player;
import com.igufguf.kingdomcraft.api.integration.MessageReceiver;
import com.igufguf.kingdomcraft.api.integration.PermissionHolder;

public interface CommandSender extends MessageReceiver, PermissionHolder {

    Player getPlayer();

    default boolean isConsole() {
        return getPlayer() == null;
    }

}
