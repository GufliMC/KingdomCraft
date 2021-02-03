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

        if ( !player.has("MENU_CHAT_CALLBACK") ) {
            return;
        }

        event.setCancelled(true);

        Consumer c = player.get("MENU_CHAT_CALLBACK", Consumer.class);
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

        if ( !player.has("MENU_CHAT_CANCEL") ) {
            return;
        }
        
        Runnable r =  player.get("MENU_CHAT_CANCEL", Runnable.class);
        plugin.getScheduler().sync().execute(() -> r.run());

        player.remove("MENU_CHAT_CANCEL");
    }

}
