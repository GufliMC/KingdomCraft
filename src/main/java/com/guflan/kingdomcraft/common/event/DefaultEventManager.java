package com.guflan.kingdomcraft.common.event;

import com.guflan.kingdomcraft.api.domain.models.Kingdom;
import com.guflan.kingdomcraft.api.domain.models.User;
import com.guflan.kingdomcraft.api.entity.Player;
import com.guflan.kingdomcraft.api.event.EventListener;
import com.guflan.kingdomcraft.api.event.EventManager;
import com.guflan.kingdomcraft.api.events.PlayerAttackPlayerEvent;

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
    public void join(Player player) {
        listeners.forEach(l -> l.onJoin(player));
    }

    @Override
    public void quit(Player player) {
        listeners.forEach(l -> l.onQuit(player));
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

    @Override
    public void playerAttack(PlayerAttackPlayerEvent event) {
        listeners.forEach(l -> l.onPlayerAttack(event));
    }
}
