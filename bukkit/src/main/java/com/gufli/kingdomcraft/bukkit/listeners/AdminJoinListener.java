/*
 * This file is part of KingdomCraft.
 *
 * KingdomCraft is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * KingdomCraft is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with KingdomCraft. If not, see <https://www.gnu.org/licenses/>.
 */

package com.gufli.kingdomcraft.bukkit.listeners;

import com.gufli.kingdomcraft.api.events.PlayerLoadedEvent;
import com.gufli.kingdomcraft.bukkit.KingdomCraftBukkitPlugin;

public class AdminJoinListener {

    private final KingdomCraftBukkitPlugin plugin;

    public AdminJoinListener(KingdomCraftBukkitPlugin plugin) {
        this.plugin = plugin;
        plugin.getKdc().getEventManager().addListener(PlayerLoadedEvent.class, this::onJoin);
    }

    public void onJoin(PlayerLoadedEvent e) {
        if ( !e.getPlayer().isAdmin() ) {
            return;
        }

        if ( !e.getPlayer().hasPermission("kingdom.admin") ) {
            e.getPlayer().setAdmin(false);
            plugin.getKdc().saveAsync(e.getPlayer().getUser());
            return;
        }

        plugin.getKdc().getMessages().send(e.getPlayer(), "adminModeJoin");
    }

}
