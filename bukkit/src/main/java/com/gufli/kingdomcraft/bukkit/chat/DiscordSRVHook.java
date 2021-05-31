package com.gufli.kingdomcraft.bukkit.chat;

import com.gufli.kingdomcraft.api.event.EventExecutor;
import com.gufli.kingdomcraft.api.events.PlayerChatEvent;
import com.gufli.kingdomcraft.bukkit.KingdomCraftBukkitPlugin;
import com.gufli.kingdomcraft.bukkit.entity.BukkitPlayer;
import github.scarsz.discordsrv.DiscordSRV;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;

public class DiscordSRVHook implements Listener {

    private final KingdomCraftBukkitPlugin plugin;
    private EventExecutor<?> eventExecutor;

    public DiscordSRVHook(KingdomCraftBukkitPlugin plugin) {
        this.plugin = plugin;

        if ( plugin.getServer().getPluginManager().isPluginEnabled("DiscordSRV") ) {
            eventExecutor = plugin.getKdc().getEventManager().addListener(PlayerChatEvent.class, this::onChat);
        }
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {
        if ( event.getPlugin().getName().equals("DiscordSRV") ) {
            eventExecutor.unregister();
        }
    }

    @EventHandler
    public void onPluginDisable(PluginEnableEvent event) {
        if ( event.getPlugin().getName().equals("DiscordSRV") ) {
            eventExecutor = plugin.getKdc().getEventManager().addListener(PlayerChatEvent.class, this::onChat);
        }
    }

    private void onChat(PlayerChatEvent event) {
        if (DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName(event.getChatChannel().getName()) == null) {
            DiscordSRV.debug("Tried looking up destination Discord channel for KingdomCraft channel " + event.getChatChannel().getName() + " but none found");
            return;
        }

        Player player = ((BukkitPlayer) event.getPlayer()).getPlayer();
        DiscordSRV.getPlugin().processChatMessage(player, event.getMessage(), event.getChatChannel().getName(), event.isCancelled());
    }

}
