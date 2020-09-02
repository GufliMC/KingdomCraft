package com.igufguf.kingdomcraft.common.chat;

import com.igufguf.kingdomcraft.api.KingdomCraftPlugin;
import com.igufguf.kingdomcraft.api.chat.ChatChannel;
import com.igufguf.kingdomcraft.api.chat.ChatManager;
import com.igufguf.kingdomcraft.api.domain.Kingdom;
import com.igufguf.kingdomcraft.api.domain.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class DefaultChatManager implements ChatManager {

    private final KingdomCraftPlugin plugin;

    private final List<ChatChannel> chatChannels = new ArrayList<>();

    public DefaultChatManager(KingdomCraftPlugin plugin) {
        this.plugin = plugin;
        plugin.getEventManager().addListener(new ChatEventListener(this));

        for ( Kingdom kingdom : plugin.getKingdomManager().getKingdoms() ) {
            KingdomChatChannel ch = new KingdomChatChannel(kingdom.getName(), kingdom);
            ch.setFormat("{player} >> {message}");
            addChatChannel(ch);
        }
    }

    @Override
    public List<ChatChannel> getChatChannels() {
        return chatChannels;
    }

    @Override
    public ChatChannel getChatChannel(String name) {
        return chatChannels.stream().filter(c -> c.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    @Override
    public void addChatChannel(ChatChannel chatChannel) {
        if ( !this.chatChannels.contains(chatChannel) ) {
            this.chatChannels.add(chatChannel);
        }
    }

    @Override
    public void removeChatChannel(ChatChannel chatChannel) {
        this.chatChannels.remove(chatChannel);
    }

    @Override
    public void handle(Player player, String message) {
        ChatChannel channel = null;

        Kingdom kingdom = player.getKingdom();
        if ( kingdom != null ) {
            // check for channels in the kingdom
            List<ChatChannel> channels = chatChannels.stream().filter(ch -> ch instanceof KingdomChatChannel)
                    .map(ch -> (KingdomChatChannel) ch)
                    .filter(ch -> ch.getKingdom() == kingdom)
                    .sorted(Comparator.comparingInt(ch -> ch.getDestinationPrefix().length()))
                    .collect(Collectors.toList());
            channel = find(player, channels, message);
        }

        if ( channel == null ) {
            // check for public channels
            List<ChatChannel> channels = chatChannels.stream().filter(ch -> !(ch instanceof KingdomChatChannel))
                    .sorted(Comparator.comparingInt(ch -> ch.getDestinationPrefix().length()))
                    .collect(Collectors.toList());
            channel = find(player, channels, message);
        }

        if ( channel == null ) {
            // TODO idk
            return;
        }

        if ( channel.getDestinationPrefix() != null ) {
            message = message.substring(channel.getDestinationPrefix().length()).trim();
        }

        send(player, channel, message);
    }

    private ChatChannel find(Player player, List<ChatChannel> channels, String message) {
        for ( ChatChannel ch : channels ) {
            if ( ch.getDestinationPrefix() != null && !message.startsWith(ch.getDestinationPrefix()) ) {
                continue;
            }

            if ( !canTalk(player, ch) ) {
                continue;
            }

            return ch;
        }
        return null;
    }

    @Override
    public void send(Player player, ChatChannel channel, String message) {
        String result = channel.getFormat();
        result = plugin.getPlaceholderManager().handle(player, result);
        result = plugin.translateColors(result);

        result = result.replace("{message}", plugin.stripColors(message));
        result = result.replace("{player}", player.getName());

        String finalResult = result;
        plugin.getPlayerManager().getOnlinePlayers().stream().filter(p -> canSee(p, channel)).forEach(p -> p.sendMessage(finalResult));
    }

    public boolean hasDefaultAccess(Player player, ChatChannel channel) {
        if ( player.hasPermission("kingdom.chat.channel.*") ) {
            return true;
        }

        if ( channel instanceof KingdomChatChannel ) {
            KingdomChatChannel ch = (KingdomChatChannel) channel;
            if ( player.getKingdom() != ch.getKingdom() ) {
                return false;
            }
        }

        if ( channel.isRestricted() && !player.hasPermission("kingdom.chat.channel." + channel.getName().toLowerCase())) {
            return false;
        }
        return true;
    }

    public boolean canSee(Player player, ChatChannel channel) {
        if ( !hasDefaultAccess(player, channel) ) {
            return false;
        }
        // TODO
        return true;
    }

    public boolean canTalk(Player player, ChatChannel channel) {
        if ( !hasDefaultAccess(player, channel) ) {
            return false;
        }
        // TODO
        return true;
    }

}
