package com.igufguf.kingdomcraft.common.event;

import com.igufguf.kingdomcraft.api.domain.Kingdom;
import com.igufguf.kingdomcraft.api.domain.Player;
import com.igufguf.kingdomcraft.api.event.EventListener;
import com.igufguf.kingdomcraft.api.event.EventManager;

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
    public void kingdomJoin(Player player) {
        listeners.forEach(l -> l.onKingdomJoin(player));
    }

    @Override
    public void kingdomLeave(Player player, Kingdom oldKingdom) {
        listeners.forEach(l -> l.onKingdomLeave(player, oldKingdom));
    }
}