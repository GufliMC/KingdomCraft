package com.guflan.kingdomcraft.api.chat;

import com.guflan.kingdomcraft.api.domain.Kingdom;

public interface ChatChannelBlueprint {

    boolean doesTarget(Kingdom kingdom);

    String getName();

    ChatChannel create(Kingdom kingdom);

}
