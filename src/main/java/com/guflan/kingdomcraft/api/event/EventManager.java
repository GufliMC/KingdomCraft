package com.guflan.kingdomcraft.api.event;

import com.guflan.kingdomcraft.api.domain.models.Kingdom;
import com.guflan.kingdomcraft.api.domain.models.User;

public interface EventManager {

    void addListener(EventListener listener);

    void removeListener(EventListener listener);

    // kingdoms

    void join(User player);

    void leave(User player);

    void kingdomJoin(User player);

    void kingdomLeave(User player, Kingdom oldKingdom);

    void kingdomCreate(Kingdom kingdom);

    void kingdomDelete(Kingdom kingdom);

}
