package com.igufguf.kingdomcraft.api.event;

import com.igufguf.kingdomcraft.api.domain.Kingdom;
import com.igufguf.kingdomcraft.api.domain.Player;

public interface EventListener {

    default void onJoin(Player player) {}

    default void onLeave(Player player) {}

    default void onKingdomJoin(Player player) {}

    default void onKingdomLeave(Player player, Kingdom oldKingdom) {}

    default void onKingdomCreate(Kingdom kingdom) {}

    default void onKingdomDelete(Kingdom kingdom) {}

}
