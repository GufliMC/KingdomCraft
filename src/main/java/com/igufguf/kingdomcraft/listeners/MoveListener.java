package com.igufguf.kingdomcraft.listeners;

import com.igufguf.kingdomcraft.KingdomCraft;
import com.igufguf.kingdomcraft.managers.TeleportManager;
import com.igufguf.kingdomcraft.objects.KingdomUser;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

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
public class MoveListener extends EventListener {

	public MoveListener(KingdomCraft plugin) {
		super(plugin);
	}

	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		if ( !isWorldEnabled(e.getPlayer().getWorld()) ) return;
		if ( e.getTo().getBlockX() == e.getFrom().getBlockX()
				&& e.getTo().getBlockY() == e.getFrom().getBlockY()
				&& e.getTo().getBlockZ() == e.getFrom().getBlockZ() ) return;

		Player p = e.getPlayer();
		KingdomUser user = plugin.getApi().getUserManager().getUser(p);
		if ( user == null ) return;

		TeleportManager tm = plugin.getApi().getTeleportManager();
		if ( !tm.isTeleporting(user) ) return;

		tm.cancelTaleporter(user);
	}

	@EventHandler
	public void onTeleport(PlayerTeleportEvent e) {

		// if 1 of the worlds is enabled and the other disabled, a change in permissions has to happen
		if ( isWorldEnabled(e.getFrom().getWorld()) ^ isWorldEnabled(e.getTo().getWorld()) ) {
			KingdomUser user = plugin.getApi().getUserManager().getUser(e.getPlayer());
			plugin.getApi().getPermissionManager().refresh(user);
		}
	}
}
