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
import com.gufli.kingdomcraft.api.entity.PlatformPlayer;
import com.gufli.kingdomcraft.api.event.EventListener;
import com.gufli.kingdomcraft.bukkit.KingdomCraftBukkitPlugin;
import com.gufli.kingdomcraft.bukkit.entity.BukkitPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class KingdomJoinQuitListener implements EventListener {

    private final KingdomCraftBukkitPlugin plugin;

    public KingdomJoinQuitListener(KingdomCraftBukkitPlugin plugin) {
        this.plugin = plugin;
        plugin.getKdc().getEventManager().addListener(this);
    }


    // kingdom join & kingdom leave commands

    @Override
    public void onKingdomJoin(User user) {
        if ( plugin.getKdc().getConfig().getOnKingdomJoinCommands().isEmpty() ) {
            return;
        }
        plugin.getScheduler().sync().execute(() -> {
            execute(user, plugin.getKdc().getConfig().getOnKingdomJoinCommands());
        });
    }

    @Override
    public void onKingdomLeave(User user, Kingdom oldKingdom) {
        if ( plugin.getKdc().getConfig().getOnKingdomLeaveCommands().isEmpty() ) {
            return;
        }
        plugin.getScheduler().sync().execute(() -> {
            execute(user, plugin.getKdc().getConfig().getOnKingdomLeaveCommands());
        });
    }

    private void execute(User user, List<String> commands) {
        Player bplayer = Bukkit.getPlayer(user.getUniqueId());

        plugin.log("Executing kingdom join/leave commands: ");
        for ( String cmd : commands ) {
            cmd = cmd.replace("{username}", user.getName());

            if ( cmd.toLowerCase().startsWith("console") ) {
                cmd = cmd.substring(7).trim();
                System.out.println(cmd);
                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), cmd);
            } else if ( bplayer != null && bplayer.isOnline() ){
                bplayer.chat("/" + cmd);
            }
        }
    }
}
