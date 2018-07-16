package com.igufguf.kingdomcraft.listeners;

import com.igufguf.kingdomcraft.KingdomCraft;
import com.igufguf.kingdomcraft.commands.executors.SpawnCommand;
import com.igufguf.kingdomcraft.objects.KingdomRank;
import com.igufguf.kingdomcraft.objects.KingdomUser;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.PermissionAttachment;

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
public class ConnectionListener extends EventListener {

	public ConnectionListener(KingdomCraft plugin) {
		super(plugin);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();

		KingdomUser user = plugin.getApi().getUserManager().getOfflineUser(p.getUniqueId());
		plugin.getApi().getUserManager().registerUser(user);

		plugin.getApi().getPermissionManager().refreshPermissions(user);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();

		KingdomUser user = plugin.getApi().getUserManager().getUser(p);
		plugin.getApi().getUserManager().unregisterUser(user);

		plugin.getApi().getUserManager().save(user);

		PermissionAttachment pa = user.hasData("permissions") ? (PermissionAttachment) user.getLocalData("permissions") : null;
		if ( pa != null ) pa.remove();
	}
	
}
