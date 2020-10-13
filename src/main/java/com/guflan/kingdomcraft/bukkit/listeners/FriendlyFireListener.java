package com.guflan.kingdomcraft.bukkit.listeners;

import com.guflan.kingdomcraft.api.KingdomCraft;
import com.guflan.kingdomcraft.api.domain.models.Kingdom;
import com.guflan.kingdomcraft.api.domain.models.User;
import com.guflan.kingdomcraft.api.entity.Player;
import com.guflan.kingdomcraft.api.events.PlayerAttackPlayerEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityEvent;

public class FriendlyFireListener implements Listener {

    private final KingdomCraft kdc;

    public FriendlyFireListener(KingdomCraft kdc) {
        this.kdc = kdc;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerDamage(EntityDamageByEntityEvent e) {
        handleEvent(e, e.getEntity(), e.getDamager());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEntityCombustByEntity(EntityCombustByEntityEvent e) {
        handleEvent(e, e.getEntity(), e.getCombuster());
    }

    private <T extends EntityEvent & Cancellable> void handleEvent(T e, Entity entity, Entity damager) {
        //if ( !isWorldEnabled(entity.getWorld()) ) return;

        Player p;
        if ( entity instanceof Player) {
            p = kdc.getPlayer(entity.getUniqueId());
        } else {
            return;
        }

        Player d;
        if ( damager instanceof Player ) {
            d = kdc.getPlayer(damager.getUniqueId());
        } else if ( damager instanceof Projectile && ((Projectile) damager).getShooter() instanceof Player) {
            d = kdc.getPlayer(((Player) ((Projectile) damager).getShooter()).getUniqueId());
        }
        else {
            return;
        }

        // Attacking an npc from another plugin will trigger this event
        if ( p == null || d == null ) {
            return;
        }

        PlayerAttackPlayerEvent event = new PlayerAttackPlayerEvent(p, d);
        kdc.getEventManager().playerAttack(event);

        if ( event.getResult() == PlayerAttackPlayerEvent.Result.DENY ) {
            e.setCancelled(true);
            return;
        }

        if ( event.getResult() == PlayerAttackPlayerEvent.Result.ALLOW ) {
            return;
        }

        User u1 = kdc.getUser(p);
        User u2 = kdc.getUser(d);

        if ( d.hasPermission("kingdom.friendlyfire.bypass") ) {
            return;
        }

        Kingdom k1 = u1.getKingdom();
        Kingdom k2 = u2.getKingdom();

        if ( k1 == null || k2 == null ) {
            return;
        }

        // TODO

        /*
        KingdomFlagHandler kfh = plugin.getApi().getFlagHandler();

        // only if friendlyfire is explicitly turned off go on
        if ( !kfh.hasFlag(k1, KingdomFlag.FRIENDLYFIRE) || kfh.getFlag(k1, KingdomFlag.FRIENDLYFIRE) ) {
            return;
        }

        // disable pvp for players in the same kingdom
        if ( k1 == k2 ) {
            notify(u1, "damageKingdom");

            e.setCancelled(true);
            if ( damager instanceof Projectile ) {
                damager.remove();
            }
            return;
        }

        // disable attacking a friendly player
        if ( plugin.getCfg().has("friendlyfire-flag-include-friendly-kingdoms") && plugin.getCfg().getBoolean("friendlyfire-flag-include-friendly-kingdoms")
                && plugin.getApi().getRelationHandler().getRelation(k1, k2) == KingdomRelation.FRIENDLY ) {
            notify(u1, "damageFriendly");

            e.setCancelled(true);
            if ( damager instanceof Projectile ) {
                damager.remove();
            }
        }
         */
    }
}
