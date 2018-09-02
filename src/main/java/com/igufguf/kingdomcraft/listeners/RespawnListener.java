package com.igufguf.kingdomcraft.listeners;

import com.igufguf.kingdomcraft.KingdomCraft;
import com.igufguf.kingdomcraft.api.models.kingdom.Kingdom;
import com.igufguf.kingdomcraft.api.models.kingdom.KingdomUser;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerRespawnEvent;

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
public class RespawnListener extends EventListener {

    public RespawnListener(KingdomCraft plugin) {
        super(plugin);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        KingdomUser user = plugin.getApi().getUserHandler().getUser(e.getPlayer());
        if ( user == null ) return;

        Kingdom kingdom = plugin.getApi().getUserHandler().getKingdom(user);
        if ( kingdom == null ) return;
        if ( kingdom.getSpawn() == null ) return;

        e.setRespawnLocation(kingdom.getSpawn());
        e.getPlayer().teleport(kingdom.getSpawn());
    }

}
