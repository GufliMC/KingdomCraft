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

import com.gufli.kingdomcraft.api.domain.Kingdom;
import com.gufli.kingdomcraft.api.entity.PlatformPlayer;
import com.gufli.kingdomcraft.api.events.PlayerLoadedEvent;
import com.gufli.kingdomcraft.api.events.UserJoinKingdomEvent;
import com.gufli.kingdomcraft.api.events.UserLeaveKingdomEvent;
import com.gufli.kingdomcraft.bukkit.KingdomCraftBukkitPlugin;
import com.gufli.kingdomcraft.bukkit.entity.BukkitPlayer;
import org.bukkit.entity.Player;

public class DisplayNameListener {


    private final KingdomCraftBukkitPlugin plugin;

    public DisplayNameListener(KingdomCraftBukkitPlugin plugin) {
        this.plugin = plugin;

        plugin.getKdc().getEventManager().addListener(PlayerLoadedEvent.class, this::onLoad);
        plugin.getKdc().getEventManager().addListener(UserJoinKingdomEvent.class, this::onKingdomJoin);
        plugin.getKdc().getEventManager().addListener(UserLeaveKingdomEvent.class, this::onKingdomLeave);
    }

    // change displayname
    public void onLoad(PlayerLoadedEvent event) {
        update(event.getPlayer());
    }

    public void onKingdomJoin(UserJoinKingdomEvent event) {
        PlatformPlayer player = plugin.getKdc().getPlayer(event.getUser());
        if ( player != null ) {
            update(player);
        }
    }

    public void onKingdomLeave(UserLeaveKingdomEvent event) {
        PlatformPlayer player = plugin.getKdc().getPlayer(event.getUser());
        if ( player != null ) {
            update(player);
        }
    }

    private void update(PlatformPlayer pp) {
        if ( !plugin.getKdc().getConfig().updateDisplayNames() ) {
            return;
        }

        String prefix = plugin.getKdc().getPlaceholderManager().handle(pp, "{kingdom_prefix}");
        if ( !prefix.equals("") && !plugin.decolorify(prefix).endsWith(" ")) {
            prefix += " ";
        }

        String suffix = plugin.getKdc().getPlaceholderManager().handle(pp, "{kingdom_suffix}");
        if ( !suffix.equals("") && !plugin.decolorify(suffix).startsWith(" ")) {
            suffix = " "+ suffix;
        }

        Player player = ((BukkitPlayer) pp).getPlayer();

        if ( prefix.equals("") && suffix.equals("") ) {
            player.setDisplayName(player.getName());
            return;
        }

        player.setDisplayName(prefix + player.getName() + suffix);
    }

}
