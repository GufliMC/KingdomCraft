package com.guflan.kingdomcraft.common.event;

import com.guflan.kingdomcraft.api.domain.Kingdom;
import com.guflan.kingdomcraft.api.entity.PlatformPlayer;
import com.guflan.kingdomcraft.api.event.EventListener;
import com.guflan.kingdomcraft.api.events.PlayerAttackPlayerEvent;

import java.util.function.Consumer;

public class EventDispatcher {

    private final EventManagerImpl eventManager;

    public EventDispatcher(EventManagerImpl eventManager) {
        this.eventManager = eventManager;
    }

    private void execute(Consumer<EventListener> consumer) {
        eventManager.listeners.forEach(listener -> {
            //
            consumer.accept(listener);
        });
    }

    public void dispatchJoin(PlatformPlayer player) {
        execute(l -> l.onJoin(player));
    }

    public void dispatchQuit(PlatformPlayer player) {
        execute(l -> l.onQuit(player));
    }

    public void dispatchKingdomJoin(PlatformPlayer player) {
        execute(l -> l.onKingdomJoin(player));
    }

    public void dispatchKingdomLeave(PlatformPlayer player, Kingdom oldKingdom) {
        execute(l -> l.onKingdomLeave(player, oldKingdom));
    }

    public void dispatchKingdomCreate(Kingdom kingdom) {
        execute(l -> l.onKingdomCreate(kingdom));
    }

    public void dispatchKingdomDelete(Kingdom kingdom) {
        execute(l -> l.onKingdomDelete(kingdom));
    }

    public void dispatchPlayerAttack(PlayerAttackPlayerEvent event) {
        execute(l -> l.onPlayerAttack(event));
    }

}
