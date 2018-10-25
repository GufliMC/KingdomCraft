package com.igufguf.kingdomcraft.listeners;

import com.igufguf.kingdomcraft.KingdomCraft;
import com.igufguf.kingdomcraft.api.events.KingdomPlayerAttackEvent;
import com.igufguf.kingdomcraft.api.handlers.KingdomFlagHandler;
import com.igufguf.kingdomcraft.api.models.flags.KingdomFlag;
import com.igufguf.kingdomcraft.api.models.kingdom.Kingdom;
import com.igufguf.kingdomcraft.api.models.kingdom.KingdomUser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

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
		if ( !isWorldEnabled(e.getEntity().getWorld()) ) return;
		if ( !(e.getEntity() instanceof Player)) return;

		Player p = (Player) e.getEntity();

		Player d;
		if ( e.getDamager() instanceof Player ) {
			d = (Player) e.getDamager();
		} else if ( e.getDamager() instanceof Projectile && ((Projectile) e.getDamager()).getShooter() instanceof Player) {
			d = (Player) ((Projectile) e.getDamager()).getShooter();
		} else {
			return;
		}

		KingdomUser u1 = plugin.getApi().getUserHandler().getUser(d);
		KingdomUser u2 = plugin.getApi().getUserHandler().getUser(p);

        KingdomPlayerAttackEvent event = new KingdomPlayerAttackEvent(e, u1, u2);

        Bukkit.getServer().getPluginManager().callEvent(event);
        e.setCancelled(event.isCancelled());

        // event was cancelled
		if ( e.isCancelled() ) return;
		if ( d.hasPermission("kingdom.friendlyfire.bypass") ) return;

		Kingdom k1 = plugin.getApi().getUserHandler().getKingdom(u1);
		Kingdom k2 = plugin.getApi().getUserHandler().getKingdom(u2);

		KingdomFlagHandler kfh = plugin.getApi().getFlagHandler();

        // only players from a different kingdom can pvp when friendlyfire
        if ( k1 == k2 && kfh.hasFlag(k1, KingdomFlag.FRIENDLYFIRE) && !kfh.getFlag(k1, KingdomFlag.FRIENDLYFIRE)  ) {
			e.setCancelled(true);
			plugin.getMsg().send(d, "damageKingdom");
        }
	}
	
}
