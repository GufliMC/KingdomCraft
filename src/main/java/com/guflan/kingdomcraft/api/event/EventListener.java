package com.guflan.kingdomcraft.api.event;

import com.guflan.kingdomcraft.api.domain.Kingdom;
import com.guflan.kingdomcraft.api.domain.Player;

public interface EventListener {

    default void onJoin(Player player) {}

    default void onLeave(Player player) {}

    default void onKingdomJoin(Player player) {}

    default void onKingdomLeave(Player player, Kingdom oldKingdom) {}

    default void onKingdomCreate(Kingdom kingdom) {}

    default void onKingdomDelete(Kingdom kingdom) {}

}
