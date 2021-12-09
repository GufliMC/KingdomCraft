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
import com.gufli.kingdomcraft.api.events.KingdomCreateEvent;
import com.gufli.kingdomcraft.api.events.KingdomDeleteEvent;
import com.gufli.kingdomcraft.bukkit.KingdomCraftBukkitPlugin;

import java.util.List;

public class KingdomCreateDeleteListener {

    private final KingdomCraftBukkitPlugin plugin;

    public KingdomCreateDeleteListener(KingdomCraftBukkitPlugin plugin) {
        this.plugin = plugin;
        plugin.getKdc().getEventManager().addListener(KingdomCreateEvent.class, this::onKingdomCreate);
        plugin.getKdc().getEventManager().addListener(KingdomDeleteEvent.class, this::onKingdomDelete);
    }


    // kingdom create & delete commands

    public void onKingdomCreate(KingdomCreateEvent e) {
        if (plugin.getKdc().getConfig().getOnKingdomCreateCommands().isEmpty()) {
            return;
        }
        plugin.getScheduler().sync().execute(() -> {
            execute(e.getKingdom(), plugin.getKdc().getConfig().getOnKingdomCreateCommands());
        });
    }

    public void onKingdomDelete(KingdomDeleteEvent e) {
        if (plugin.getKdc().getConfig().getOnKingdomDeleteCommands().isEmpty()) {
            return;
        }
        plugin.getScheduler().sync().execute(() -> {
            execute(e.getKingdom(), plugin.getKdc().getConfig().getOnKingdomDeleteCommands());
        });
    }

    private void execute(Kingdom kingdom, List<String> commands) {
        plugin.log("Executing kingdom create/delete commands: ");
        for (String cmd : commands) {
            cmd = cmd.replace("{kingdom}", kingdom.getDisplay());
            cmd = cmd.replace("{kingdom_name}", kingdom.getName());
            if (cmd.toLowerCase().startsWith("console")) {
                cmd = cmd.substring(7).trim();
            }

            plugin.log(cmd);
            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), cmd);
        }
    }
}
