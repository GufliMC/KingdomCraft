package com.igufguf.kingdomcraft.api.integration;

import com.igufguf.kingdomcraft.api.domain.Player;

public interface OnlinePlayer extends MessageReceiver, PermissionHolder {

    Player getPlayer();

}
