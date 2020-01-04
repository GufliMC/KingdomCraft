package com.igufguf.kingdomcraft.api.events;

import com.igufguf.kingdomcraft.api.models.kingdom.KingdomUser;
import org.bukkit.event.Cancellable;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityEvent;

/**
 * Created by Joris on 20/07/2018 in project KingdomCraft.
 */
public class KingdomPlayerAttackEvent extends KingdomEvent implements Cancellable {

    private boolean cancelled = false;

    private EntityEvent originalEvent;

    private KingdomUser damager;
    private KingdomUser target;

    public KingdomPlayerAttackEvent(EntityEvent originalEvent, KingdomUser damager, KingdomUser target) {
        this.originalEvent = originalEvent;
        this.damager = damager;
        this.target = target;
    }

    public KingdomUser getDamager() {
        return damager;
    }

    public KingdomUser getTarget() {
        return target;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }

    public EntityEvent getOriginalEvent() {
        return originalEvent;
    }

}
