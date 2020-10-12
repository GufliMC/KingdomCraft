package com.guflan.kingdomcraft.api.event;

import com.guflan.kingdomcraft.api.domain.models.Kingdom;
import com.guflan.kingdomcraft.api.domain.models.User;

public interface EventListener {

    default void onJoin(User player) {}

    default void onLeave(User player) {}

    default void onKingdomJoin(User player) {}

    default void onKingdomLeave(User player, Kingdom oldKingdom) {}

    default void onKingdomCreate(Kingdom kingdom) {}

    default void onKingdomDelete(Kingdom kingdom) {}

}
