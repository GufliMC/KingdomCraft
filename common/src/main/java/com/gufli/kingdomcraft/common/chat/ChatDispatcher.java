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

package com.gufli.kingdomcraft.common.chat;

import com.gufli.kingdomcraft.api.chat.ChatChannel;
import com.gufli.kingdomcraft.api.entity.PlatformPlayer;
import com.gufli.kingdomcraft.api.events.PlayerChatEvent;
import com.gufli.kingdomcraft.common.KingdomCraftImpl;
import org.apache.commons.text.StringEscapeUtils;

import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ChatDispatcher {

    private final KingdomCraftImpl kdc;
    private final ChatManagerImpl chatManager;

    public ChatDispatcher(KingdomCraftImpl kdc, ChatManagerImpl chatManager) {
        this.kdc = kdc;
        this.chatManager = chatManager;
    }

    public void handle(PlatformPlayer player, String message) {
        List<ChatChannel> channels = chatManager.getChatChannels().stream()
                .filter(c -> chatManager.canTalk(player, c))
                .sorted(Comparator.comparingInt(ch -> ch.getPrefix() == null ? 0 : -ch.getPrefix().length()))
                .collect(Collectors.toList());

        String strippedMessage = kdc.getPlugin().decolorify(message);

        ChatChannel channel = null;
        if (player.has("DEFAULT_CHATCHANNEL") ) {
            channel = channels.stream().filter(ch -> ch.getName().equals(player.get("DEFAULT_CHATCHANNEL", String.class)))
                    .findFirst().orElse(null);
        }

        for ( ChatChannel ch : channels ) {
            if ( ch == channel ) {
                continue;
            }

            if ( ch.getPrefix() != null && !strippedMessage.startsWith(ch.getPrefix()) ) {
                continue;
            }

            // If the player is using a default channel, use that prefix instead to talk in the channel without prefix
            if ( channel != null && channel.getPrefix() != null && ch.getPrefix() != null && ch.getPrefix().equals("") ) {
                if ( strippedMessage.startsWith(channel.getPrefix()) ) {
                    message = message.replaceFirst(Pattern.quote(channel.getPrefix()), "");
                    channel = ch;
                    break;
                }
                continue;
            }

            if ( ch.getPrefix() != null ) {
                message = message.replaceFirst(Pattern.quote(ch.getPrefix()), "");
            }
            channel = ch;
            break;
        }

        if ( channel == null ) {
            if (chatManager.getDefaultChatChannel() == null) {
                kdc.getMessageManager().send(player, "chatNoChannel");
                return;
            }

            channel = chatManager.getDefaultChatChannel();
        }

        // Chat cooldown check
        String cooldownKey = "CHAT_COOLDOWN_" + channel.getName();
        if ( channel.getCooldown() > 0 && player.has(cooldownKey)
                && !player.isAdmin() && !player.hasPermission("kingdom.chat.bypass.cooldown")) {

            long lastMessage = player.get(cooldownKey, Long.class);
            long diff = System.currentTimeMillis() - lastMessage;
            if ( diff < channel.getCooldown() * 1000L) {
                float remaining = ((channel.getCooldown() * 1000) - diff) / 1000f;
                DecimalFormat df = new DecimalFormat("0.0");
                kdc.getMessageManager().send(player, "chatChannelCooldown", df.format(remaining));
                return;
            }
        }

        PlayerChatEvent event = new PlayerChatEvent(player, channel, message);
        kdc.getEventDispatcher().dispatchPlayerChat(event);

        if ( event.isCancelled() ) {
            return;
        }

        send(player, channel, event.getMessage());
    }

    public void send(PlatformPlayer player, ChatChannel channel, String message) {
        String result = channel.getFormat();
        result = StringEscapeUtils.unescapeJava(result);
        result = kdc.getPlaceholderManager().handle(player, result);
        result = kdc.getMessageManager().colorify(result);

        if ( player.hasPermission("kingdom.chat.colors") ) {
            message = kdc.getMessageManager().colorify(message);
        }

        result = kdc.getPlaceholderManager().strip(result, "message", "player");
        result = result.replace("{message}", message);
        result = result.replace("{player}", player.getName());

        String finalResult = result;
        List<PlatformPlayer> receivers = kdc.getOnlinePlayers().stream()
                .filter(p -> chatManager.canRead(p, channel))
                .filter(p -> channel.getRange() <= 0 || p.getLocation().distanceTo(player.getLocation()) <= channel.getRange())
                .collect(Collectors.toList());

        if ( channel.getCooldown() > 0 && !player.isAdmin() && !player.hasPermission("kingdom.chat.bypass.cooldown") ) {
            player.set("CHAT_COOLDOWN_" + channel.getName(), System.currentTimeMillis());
        }

        if ( !receivers.contains(player) ) {
            receivers.add(player);
        }

        for ( PlatformPlayer p : receivers ) {
            p.sendMessage(finalResult);
        }

        kdc.getOnlinePlayers().stream()
                .filter(p -> p.has("SOCIAL_SPY") && p.get("SOCIAL_SPY", Boolean.class))
                .filter(p -> !receivers.contains(p))
                .forEach(p -> {
            p.sendMessage(kdc.getMessageManager().getMessage("socialSpyPrefix") + finalResult);
        });

        System.out.println("[" + channel.getName() + "] " + player.getName() + ": " + message);
    }

}
