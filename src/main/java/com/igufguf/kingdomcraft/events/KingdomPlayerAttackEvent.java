package com.igufguf.kingdomcraft.events;

import com.igufguf.kingdomcraft.objects.KingdomUser;
import org.bukkit.event.Cancellable;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * Created by Joris on 20/07/2018 in project KingdomCraft.
 */
public class KingdomPlayerAttackEvent extends KingdomEvent implements Cancellable {

    private boolean cancelled = false;

    private EntityDamageEvent originalEvent;

    private KingdomUser damager;
    private KingdomUser target;

    public KingdomPlayerAttackEvent(EntityDamageEvent originalEvent, KingdomUser damager, KingdomUser target) {
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

    // overrides

    public double getOriginalDamage(EntityDamageEvent.DamageModifier type) {
        return originalEvent.getOriginalDamage(type);
    }

    public void setDamage(EntityDamageEvent.DamageModifier type, double damage) {
        originalEvent.setDamage(type, damage);
    }

    public double getDamage(EntityDamageEvent.DamageModifier type) {
        return originalEvent.getDamage(type);
    }

    public boolean isApplicable(EntityDamageEvent.DamageModifier type)  {
        return originalEvent.isApplicable(type);
    }

    public double getDamage() {
        return originalEvent.getDamage();
    }

    public final double getFinalDamage() {
        return originalEvent.getFinalDamage();
    }

    public void setDamage(double damage) {
        originalEvent.setDamage(damage);
    }
}
