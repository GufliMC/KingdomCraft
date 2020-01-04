package com.igufguf.kingdomcraft.listeners;

import com.igufguf.kingdomcraft.KingdomCraft;
import com.igufguf.kingdomcraft.api.events.KingdomPlayerAttackEvent;
import com.igufguf.kingdomcraft.api.handlers.KingdomFlagHandler;
import com.igufguf.kingdomcraft.api.models.flags.KingdomFlag;
import com.igufguf.kingdomcraft.api.models.kingdom.Kingdom;
import com.igufguf.kingdomcraft.api.models.kingdom.KingdomRelation;
import com.igufguf.kingdomcraft.api.models.kingdom.KingdomUser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityEvent;

/**
 * Copyrighted 2018 iGufGuf
 *
 * This file is part of KingdomCraft.
 *
 * Kingdomcraft is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * KingdomCraft is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with KingdomCraft.  If not, see <http://www.gnu.org/licenses/>.
 *
 **/
public class DamageListener extends EventListener {

	public DamageListener(KingdomCraft plugin) {
		super(plugin);
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
		if ( !isWorldEnabled(entity.getWorld()) ) return;

		Player p;
		if ( entity instanceof Player) {
			p = (Player) entity;
		} else {
			return;
		}

		Player d;
		if ( damager instanceof Player ) {
			d = (Player) damager;
		} else if ( damager instanceof Projectile && ((Projectile) damager).getShooter() instanceof Player) {
			d = (Player) ((Projectile) damager).getShooter();
		}
		else {
			return;
		}

		KingdomUser u1 = plugin.getApi().getUserHandler().getUser(d);
		KingdomUser u2 = plugin.getApi().getUserHandler().getUser(p);

		KingdomPlayerAttackEvent event = new KingdomPlayerAttackEvent(e, u1, u2);

		Bukkit.getServer().getPluginManager().callEvent(event);
		e.setCancelled(event.isCancelled());

		// event was cancelled
		if ( e.isCancelled() ) return;
		if ( d.hasPermission("kingdom.friendlyfire.bypass") // TODO remove old permission in future version
				|| d.hasPermission("kingdom.flag.bypass.friendlyfire")) return;

		Kingdom k1 = plugin.getApi().getUserHandler().getKingdom(u1);
		Kingdom k2 = plugin.getApi().getUserHandler().getKingdom(u2);
		if ( k1 == null || k2 == null ) return;

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
	}

	private final static long NOTIFY_DELAY = 5000L; // 5 seconds
	private final static String NOTIFY_KEY = "friendlyfire-notify";

	private void notify(KingdomUser d, String msg) {
		long lastNotification = 0;

		if ( d.hasMemory(NOTIFY_KEY) ) {
			lastNotification = d.getMemory(NOTIFY_KEY, Long.class);
		}

		if ( System.currentTimeMillis() - lastNotification > NOTIFY_DELAY ) {
			plugin.getMsg().send(d.getPlayer(), msg);
			d.setMemory(NOTIFY_KEY, System.currentTimeMillis());
		}
	}
	
}
