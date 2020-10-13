package com.guflan.kingdomcraft.api.event;

import com.guflan.kingdomcraft.api.domain.models.Kingdom;
import com.guflan.kingdomcraft.api.domain.models.User;
import com.guflan.kingdomcraft.api.entity.Player;
import com.guflan.kingdomcraft.api.events.PlayerAttackPlayerEvent;

public interface EventListener {

    default void onJoin(Player player) {}

    default void onQuit(Player player) {}

    default void onKingdomJoin(User player) {}

    default void onKingdomLeave(User player, Kingdom oldKingdom) {}

    default void onKingdomCreate(Kingdom kingdom) {}

    default void onKingdomDelete(Kingdom kingdom) {}

    default void onPlayerAttack(PlayerAttackPlayerEvent event) {}
}
