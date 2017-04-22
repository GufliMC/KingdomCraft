package com.igufguf.kingdomcraft.listeners;

import com.igufguf.kingdomcraft.KingdomCraft;
import com.igufguf.kingdomcraft.commands.executors.SpawnCommand;
import com.igufguf.kingdomcraft.KingdomCraftMessages;
import com.igufguf.kingdomcraft.objects.KingdomUser;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 * Copyrighted 2017 iGufGuf
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

	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		if ( !enabledWorld(e.getPlayer().getWorld()) ) return;
		
		if ( SpawnCommand.teleporting.contains(e.getPlayer().getName()) ) {
			if ( !e.getFrom().getBlock().equals(e.getTo().getBlock()) ) {
				SpawnCommand.teleporting.remove(e.getPlayer().getName());
				e.getPlayer().sendMessage(KingdomCraft.prefix + KingdomCraft.getMsg().getMessage("teleportCancel"));
			}
		}
	}

	@EventHandler
	public void onTeleport(PlayerTeleportEvent e) {
		if ( (!enabledWorld(e.getFrom().getWorld()) && enabledWorld(e.getTo().getWorld()))
				|| ( !enabledWorld(e.getTo().getWorld()) && enabledWorld(e.getFrom().getWorld()))) {
			KingdomUser user = KingdomCraft.getApi().getUser(e.getPlayer());
			KingdomCraft.getApi().refreshPermissions(user);
		}
	}
}
