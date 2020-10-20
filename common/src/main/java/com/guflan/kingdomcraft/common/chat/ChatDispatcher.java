package com.guflan.kingdomcraft.common.chat;

import com.guflan.kingdomcraft.api.KingdomCraft;
import com.guflan.kingdomcraft.api.chat.ChatChannel;
import com.guflan.kingdomcraft.api.entity.PlatformPlayer;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ChatDispatcher {

    private final KingdomCraft kdc;
    private final ChatManagerImpl chatManager;

    public ChatDispatcher(KingdomCraft kdc, ChatManagerImpl chatManager) {
        this.kdc = kdc;
        this.chatManager = chatManager;
    }

    public void handle(PlatformPlayer player, String message) {
        List<ChatChannel> channels = chatManager.getChatChannels().stream()
                .filter(c -> chatManager.canAccess(player, c))
                .sorted(Comparator.comparingInt(ch -> ch.getPrefix() == null ? 0 : -ch.getPrefix().length()))
                .collect(Collectors.toList());

        ChatChannel channel = null;
        for ( ChatChannel ch : channels ) {
            if ( ch.getPrefix() != null && !message.startsWith(ch.getPrefix()) ) {
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
            if ( !chatManager.canSee(player, channel) ) {
                kdc.getMessageManager().send(player, "chatChannelDisabled", channel.getName());
            }
            if ( channel.getPrefix() != null ) {
                message = message.substring(channel.getPrefix().length()).trim();
            }
        }

        send(player, channel, message);
    }

    public void send(PlatformPlayer player, ChatChannel channel, String message) {
        String result = channel.getFormat();
        result = kdc.getPlaceholderManager().handle(player, result);
        result = kdc.getMessageManager().colorify(result);

        message = kdc.getMessageManager().colorify(message);
        if ( !player.hasPermission("kingdom.chat.colors") ) {
            message = kdc.getMessageManager().decolorify(message);
        }

        result = result.replace("{message}", message);
        result = result.replace("{player}", player.getName());

        String finalResult = kdc.getPlaceholderManager().strip(result);
        kdc.getOnlinePlayers().stream().filter(p -> chatManager.canSee(p, channel))
                .filter(p -> channel.getRange() <= 0 || p.getLocation().distanceTo(player.getLocation()) <= channel.getRange())
                .forEach(p -> p.sendMessage(finalResult));

        System.out.println("[CHAT] " + kdc.getMessageManager().decolorify(finalResult));
    }

}
