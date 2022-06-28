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

package com.gufli.kingdomcraft.bukkit.chat;

import com.gufli.kingdomcraft.api.chat.ChatChannel;
import com.gufli.kingdomcraft.api.entity.PlatformPlayer;
import com.gufli.kingdomcraft.api.event.EventSubscription;
import com.gufli.kingdomcraft.api.events.PlayerChatEvent;
import com.gufli.kingdomcraft.bukkit.KingdomCraftBukkitPlugin;
import com.gufli.kingdomcraft.bukkit.entity.BukkitPlayer;
import com.gufli.kingdomcraft.bukkit.entity.OfflineBukkitPlayer;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.DiscordGuildMessagePreProcessEvent;
import github.scarsz.discordsrv.dependencies.jda.api.entities.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;

import java.util.UUID;

public class DiscordSRVHook implements Listener {

    private final KingdomCraftBukkitPlugin plugin;
    private EventSubscription<?> subscription;

    public DiscordSRVHook(KingdomCraftBukkitPlugin plugin) {
        this.plugin = plugin;

        if (!plugin.getServer().getPluginManager().isPluginEnabled("DiscordSRV")) {
            return;
        }

        enable();
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {
        if (event.getPlugin().getName().equals("DiscordSRV")) {
            disable();
        }
    }

    //

    private void enable() {
        subscription = plugin.getKdc().getEventManager().addListener(PlayerChatEvent.class, this::onChat);
        DiscordSRV.api.subscribe(this);
    }

    private void disable() {
        subscription.unregister();
        DiscordSRV.api.unsubscribe(this);
    }

    //

    // PlayerChatEvent from kdc should be async following the PlayerAsyncChatEvent from bukkit
    private void onChat(PlayerChatEvent event) {
        if (DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName(event.getChatChannel().getName()) == null) {
            DiscordSRV.debug("Tried looking up destination Discord channel for KingdomCraft channel " + event.getChatChannel().getName() + " but none found.");
            return;
        }

        if ( event.getPlayer().has("DISCORDSRV_IGNORE")
                && event.getMessage().equals(event.getPlayer().get("DISCORDSRV_IGNORE", String.class)) ) {
            event.getPlayer().remove("DISCORDSRV_IGNORE");
            return;
        }

        Player player = Bukkit.getPlayer(event.getPlayer().getUniqueId());
        if ( player == null ) {
            return;
        }
        DiscordSRV.getPlugin().processChatMessage(player, event.getMessage(), event.getChatChannel().getName(), event.isCancelled());
    }

    @Subscribe
    public void onDiscordChat(DiscordGuildMessagePreProcessEvent event) {
        if (event.isCancelled()) {
            return;
        }

        String channel = DiscordSRV.getPlugin().getDestinationGameChannelNameForTextChannel(event.getChannel());
        if (channel == null) {
            DiscordSRV.debug("Tried looking up destination KingdomCraft channel for Discord channel " + event.getChannel().getName() + " but none found.");
            return;
        }

        ChatChannel cc = plugin.getKdc().getChatManager().getChatChannel(channel);
        if (cc == null) {
            return;
        }

        plugin.getScheduler().makeAsyncFuture(() -> {
            User discordUser = event.getAuthor();
            UUID uuid = DiscordSRV.getPlugin().getAccountLinkManager().getUuid(discordUser.getId());
            if (uuid == null) {
                return;
            }

            com.gufli.kingdomcraft.api.domain.User kingdomUser = plugin.getKdc().getUser(uuid).join();
            if (kingdomUser == null) {
                return;
            }

            PlatformPlayer pp = plugin.getKdc().getPlayer(kingdomUser);
            if (pp == null) {
                // very hacky yes yes
                pp = new OfflineBukkitPlayer(Bukkit.getOfflinePlayer(uuid), kingdomUser);
            }

            String message = event.getMessage().getContentStripped();

            pp.set("DISCORDSRV_IGNORE", message);
            plugin.getKdc().getChatManager().dispatch(pp, cc, message);
        }).exceptionally(e -> {
            e.printStackTrace();
            return null;
        });
    }

}
