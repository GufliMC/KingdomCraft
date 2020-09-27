package com.guflan.kingdomcraft.common.chat;

import com.guflan.kingdomcraft.api.domain.Kingdom;

public class KingdomChatChannel extends DefaultChatChannel {

    private final Kingdom kingdom;

    public KingdomChatChannel(String name, Kingdom kingdom) {
        super(name);
        this.kingdom = kingdom;
    }

    public Kingdom getKingdom() {
        return kingdom;
    }
}
