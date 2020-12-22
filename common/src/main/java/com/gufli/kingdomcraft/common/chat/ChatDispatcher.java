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
        for ( ChatChannel ch : channels ) {
            if ( ch.getPrefix() != null && !strippedMessage.startsWith(ch.getPrefix()) ) {
                continue;
            }
            channel = ch;
            break;
        }

        if ( channel == null ) {
            if ( chatManager.getDefaultChatChannel() == null ) {
                kdc.getMessageManager().send(player, "chatNoChannel");
                return;
            }

            channel = chatManager.getDefaultChatChannel();
        } else {
            if ( !chatManager.canTalk(player, channel) ) {
                kdc.getMessageManager().send(player, "chatChannelDisabled", channel.getName());
            }
            if ( channel.getPrefix() != null ) {
                message = message.replaceFirst(Pattern.quote(channel.getPrefix()), "");
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

        if ( !receivers.contains(player) ) {
            receivers.add(player);
        }

        for ( PlatformPlayer p : receivers ) {
            p.sendMessage(finalResult);
        }

        kdc.getOnlinePlayers().stream().filter(PlatformPlayer::isAdmin).filter(p -> !receivers.contains(p)).forEach(p -> {
            p.sendMessage(kdc.getMessageManager().getMessage("socialSpyPrefix") + finalResult);
        });

        System.out.println("[" + channel.getName() + "] " + player.getName() + ": " + message);
    }

}
