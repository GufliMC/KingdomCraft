package com.igufguf.kingdomcraft.listeners;

import com.igufguf.kingdomcraft.KingdomCraft;
import com.igufguf.kingdomcraft.objects.KingdomUser;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
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

	@EventHandler(ignoreCancelled = true)
	public void onPlayerDamage(EntityDamageByEntityEvent e) {
		if ( !isWorldEnabled(e.getEntity().getWorld()) ) return;
		if ( plugin.getCfg().getBoolean("friendlyfire") ) return;
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

		if ( d.hasPermission("kingdom.friendlyfire.bypass") ) return;

		KingdomUser u1 = plugin.getApi().getUserManager().getUser(p);
		KingdomUser u2 = plugin.getApi().getUserManager().getUser(d);

		if ( u1.getKingdom() == null ) return;

		if ( u2.getKingdom() != null ) {
			if ( u1.getKingdom().equals(u2.getKingdom()) ) {
				plugin.getMsg().send(d, "damageKingdom");
				e.setCancelled(true);
			}
		}
	}
	
}
