package com.guflan.kingdomcraft.api.event;

import com.guflan.kingdomcraft.api.domain.Kingdom;
import com.guflan.kingdomcraft.api.domain.Player;

public interface EventManager {

    void addListener(EventListener listener);

    void removeListener(EventListener listener);

    // kingdoms

    void join(Player player);

    void leave(Player player);

    void kingdomJoin(Player player);

    void kingdomLeave(Player player, Kingdom oldKingdom);

    void kingdomCreate(Kingdom kingdom);

    void kingdomDelete(Kingdom kingdom);

}
