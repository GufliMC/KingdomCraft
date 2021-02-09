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
import com.gufli.kingdomcraft.api.domain.User;
import com.gufli.kingdomcraft.api.events.UserJoinKingdomEvent;
import com.gufli.kingdomcraft.api.events.UserLeaveKingdomEvent;
import com.gufli.kingdomcraft.bukkit.KingdomCraftBukkitPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class KingdomJoinQuitListener {

    private final KingdomCraftBukkitPlugin plugin;

    public KingdomJoinQuitListener(KingdomCraftBukkitPlugin plugin) {
        this.plugin = plugin;
        plugin.getKdc().getEventManager().addListener(UserJoinKingdomEvent.class, this::onKingdomJoin);
        plugin.getKdc().getEventManager().addListener(UserLeaveKingdomEvent.class, this::onKingdomLeave);
    }


    // kingdom join & kingdom leave commands

    public void onKingdomJoin(UserJoinKingdomEvent e) {
        if ( plugin.getKdc().getConfig().getOnKingdomJoinCommands().isEmpty() ) {
            return;
        }
        plugin.getScheduler().sync().execute(() -> {
            execute(e.getUser(), e.getUser().getKingdom(), plugin.getKdc().getConfig().getOnKingdomJoinCommands());
        });
    }

    public void onKingdomLeave(UserLeaveKingdomEvent e) {
        if ( plugin.getKdc().getConfig().getOnKingdomLeaveCommands().isEmpty() ) {
            return;
        }
        plugin.getScheduler().sync().execute(() -> {
            execute(e.getUser(), e.getKingdom(), plugin.getKdc().getConfig().getOnKingdomLeaveCommands());
        });
    }

    private void execute(User user, Kingdom kingdom, List<String> commands) {
        Player bplayer = Bukkit.getPlayer(user.getUniqueId());

        plugin.log("Executing kingdom join/leave commands: ");
        for ( String cmd : commands ) {
            cmd = cmd.replace("{username}", user.getName());
            cmd = cmd.replace("{kingdom_name}", kingdom.getName());
            cmd = cmd.replace("{kingdom}", plugin.colorify(kingdom.getDisplay()));
            cmd = cmd.replace("{kingdom_prefix}", plugin.colorify(kingdom.getPrefix()));
            cmd = cmd.replace("{kingdom_suffix}", plugin.colorify(kingdom.getSuffix()));
            cmd = plugin.getKdc().getPlaceholderManager().handle(user, cmd);

            if ( cmd.toLowerCase().startsWith("console") ) {
                cmd = cmd.substring(7).trim();
                plugin.log(cmd);
                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), cmd);
            } else if ( bplayer != null && bplayer.isOnline() ){
                bplayer.chat("/" + cmd);
            }
        }
    }
}
