package com.guflan.kingdomcraft.common.event;

import com.guflan.kingdomcraft.api.domain.Kingdom;
import com.guflan.kingdomcraft.api.domain.User;
import com.guflan.kingdomcraft.api.event.EventListener;
import com.guflan.kingdomcraft.api.event.EventManager;

import java.util.ArrayList;
import java.util.List;

public class DefaultEventManager implements EventManager {

    private final List<EventListener> listeners = new ArrayList<>();

    @Override
    public void addListener(EventListener listener) {
        if ( !listeners.contains(listener) ) {
            this.listeners.add(listener);
        }
    }

    @Override
    public void removeListener(EventListener listener) {
        this.listeners.remove(listener);
    }

    @Override
    public void join(User player) {
        listeners.forEach(l -> l.onJoin(player));
    }

    @Override
    public void leave(User player) {
        listeners.forEach(l -> l.onLeave(player));
    }

    @Override
    public void kingdomJoin(User player) {
        listeners.forEach(l -> l.onKingdomJoin(player));
    }

    @Override
    public void kingdomLeave(User player, Kingdom oldKingdom) {
        listeners.forEach(l -> l.onKingdomLeave(player, oldKingdom));
    }

    @Override
    public void kingdomCreate(Kingdom kingdom) {
        listeners.forEach(l -> l.onKingdomCreate(kingdom));
    }

    @Override
    public void kingdomDelete(Kingdom kingdom) {
        listeners.forEach(l -> l.onKingdomDelete(kingdom));
    }
}
