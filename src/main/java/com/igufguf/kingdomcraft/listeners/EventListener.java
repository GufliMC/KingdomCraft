package com.igufguf.kingdomcraft.listeners;

import com.igufguf.kingdomcraft.KingdomCraft;
import com.igufguf.kingdomcraft.objects.KingdomObject;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

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
class EventListener implements Listener {

	public final KingdomCraft plugin;

	public EventListener(KingdomCraft plugin) {
		this.plugin = plugin;
	}

	public KingdomObject getKingdom(String kdname) {
		return plugin.getApi().getKingdomManager().getKingdom(kdname);
	}
	
	public boolean kingdomExists(String kdname) {
		return plugin.getApi().getKingdomManager().getKingdom(kdname) != null;
	}
	
	public KingdomObject getKingdom(Player p) {
		return plugin.getApi().getUserManager().getKingdom(plugin.getApi().getUserManager().getUser(p));
	}

	public boolean isWorldEnabled(World world) {
		return plugin.getApi().isWorldEnabled(world);
	}
	
}
