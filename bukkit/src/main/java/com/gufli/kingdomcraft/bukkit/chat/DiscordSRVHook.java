package com.gufli.kingdomcraft.bukkit.chat;

import com.gufli.kingdomcraft.api.events.PlayerChatEvent;
import com.gufli.kingdomcraft.bukkit.KingdomCraftBukkitPlugin;
import com.gufli.kingdomcraft.bukkit.entity.BukkitPlayer;
import github.scarsz.discordsrv.DiscordSRV;
import org.bukkit.entity.Player;

public class DiscordSRVHook {

    public DiscordSRVHook(KingdomCraftBukkitPlugin plugin) {
        plugin.getKdc().getEventManager().addListener(PlayerChatEvent.class, this::onChat);
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
