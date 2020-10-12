package com.guflan.kingdomcraft.api.chat;

import com.guflan.kingdomcraft.api.domain.models.Kingdom;

public interface ChatChannelBlueprint {

    boolean doesTarget(Kingdom kingdom);

    String getName();

    ChatChannel create(Kingdom kingdom);

}
