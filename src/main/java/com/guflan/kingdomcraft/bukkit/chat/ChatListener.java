package com.guflan.kingdomcraft.bukkit.chat;

import com.guflan.kingdomcraft.api.KingdomCraftPlugin;
import com.guflan.kingdomcraft.api.domain.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

    private final KingdomCraftPlugin plugin;

    public ChatListener(KingdomCraftPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = plugin.getPlayerManager().getPlayer(event.getPlayer().getUniqueId());
        plugin.getChatManager().handle(player, event.getMessage());
        event.setCancelled(true);
    }
}
