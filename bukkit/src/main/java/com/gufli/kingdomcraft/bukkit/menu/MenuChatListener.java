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

package com.gufli.kingdomcraft.bukkit.menu;

import com.gufli.kingdomcraft.api.entity.PlatformPlayer;
import com.gufli.kingdomcraft.bukkit.KingdomCraftBukkitPlugin;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.function.Consumer;

public class MenuChatListener implements Listener {

    private final KingdomCraftBukkitPlugin plugin;

    public MenuChatListener(KingdomCraftBukkitPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        PlatformPlayer player = plugin.getKdc().getPlayer(event.getPlayer().getUniqueId());
        Consumer<String> c = (Consumer<String>)player.get("MENU_CHAT_CALLBACK", Consumer.class);

        if ( c == null ) {
            return;
        }

        event.setCancelled(true);

        player.remove("MENU_CHAT_CALLBACK");

        if ( !event.getMessage().equalsIgnoreCase("cancel") ) {
            try {
                plugin.getScheduler().sync().execute(() -> c.accept(event.getMessage()));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return;
        }

        player.sendMessage(ChatColor.GREEN + "Cancelled!");

        Runnable r =  player.get("MENU_CHAT_CANCEL", Runnable.class);

        if ( r == null ) {
            return;
        }
        
        plugin.getScheduler().sync().execute(() -> r.run());

        player.remove("MENU_CHAT_CANCEL");
    }

}
