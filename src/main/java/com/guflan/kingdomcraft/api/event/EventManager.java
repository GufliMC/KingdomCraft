package com.guflan.kingdomcraft.api.event;

import com.guflan.kingdomcraft.api.domain.models.Kingdom;
import com.guflan.kingdomcraft.api.domain.models.User;
import com.guflan.kingdomcraft.api.entity.Player;
import com.guflan.kingdomcraft.api.events.PlayerAttackPlayerEvent;

public interface EventManager {

    void addListener(EventListener listener);

    void removeListener(EventListener listener);

    // kingdoms

    void join(Player player);

    void quit(Player player);

    void kingdomJoin(User player);

    void kingdomLeave(User player, Kingdom oldKingdom);

    void kingdomCreate(Kingdom kingdom);

    void kingdomDelete(Kingdom kingdom);

    void playerAttack(PlayerAttackPlayerEvent event);

}
