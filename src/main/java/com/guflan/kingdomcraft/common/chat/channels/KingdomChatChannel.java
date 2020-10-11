package com.guflan.kingdomcraft.common.chat.channels;

import com.guflan.kingdomcraft.api.domain.Kingdom;

import java.util.Arrays;
import java.util.List;

public class KingdomChatChannel extends BasicChatChannel {

    private final List<Kingdom> kingdoms;

    public KingdomChatChannel(String name, Kingdom... kingdoms) {
        super(name);
        this.kingdoms = Arrays.asList(kingdoms);
    }

    public KingdomChatChannel(String name, List<Kingdom> kingdoms) {
        super(name);
        this.kingdoms = kingdoms;
    }

    public List<Kingdom> getKingdoms() {
        return kingdoms;
    }
}
