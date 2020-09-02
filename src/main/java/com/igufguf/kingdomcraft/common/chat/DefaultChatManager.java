package com.igufguf.kingdomcraft.common.chat;

import com.igufguf.kingdomcraft.api.KingdomCraftPlugin;
import com.igufguf.kingdomcraft.api.chat.ChatChannel;
import com.igufguf.kingdomcraft.api.chat.ChatManager;
import com.igufguf.kingdomcraft.api.domain.Kingdom;
import com.igufguf.kingdomcraft.api.domain.Player;
import com.igufguf.kingdomcraft.api.integration.OnlinePlayer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class DefaultChatManager implements ChatManager {

    private final KingdomCraftPlugin plugin;

    private final List<ChatChannel> chatChannels = new ArrayList<>();

    public DefaultChatManager(KingdomCraftPlugin plugin) {
        this.plugin = plugin;
        new ChatEventListener(this);

        for ( Kingdom kingdom : plugin.getKingdomManager().getKingdoms() ) {
            KingdomChatChannel ch = new KingdomChatChannel(kingdom.getName(), kingdom);
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
    public void handle(OnlinePlayer oplayer, String message) {

        Kingdom kingdom = oplayer.getPlayer().getKingdom();
        if ( kingdom != null ) {
            // check for channels in the kingdom
            List<ChatChannel> channels = chatChannels.stream().filter(ch -> ch instanceof KingdomChatChannel)
                    .map(ch -> (KingdomChatChannel) ch)
                    .filter(ch -> ch.getKingdom() == kingdom)
                    .sorted(Comparator.comparingInt(ch -> ch.getDestinationPrefix().length()))
                    .collect(Collectors.toList());

            ChatChannel ch = find(oplayer, channels, message);
            if ( ch != null ) {
                send(oplayer, ch, message.substring(ch.getDestinationPrefix().length()).trim());
                return;
            }
        }

        // check for public channels
        List<ChatChannel> channels = chatChannels.stream().filter(ch -> !(ch instanceof KingdomChatChannel))
                .sorted(Comparator.comparingInt(ch -> ch.getDestinationPrefix().length()))
                .collect(Collectors.toList());

        ChatChannel ch = find(oplayer, channels, message);
        if ( ch != null ) {
            send(oplayer, ch, message.substring(ch.getDestinationPrefix().length()).trim());
            return;
        }
    }

    private ChatChannel find(OnlinePlayer oplayer, List<ChatChannel> channels, String message) {
        for ( ChatChannel ch : channels ) {
            if ( !message.startsWith(ch.getDestinationPrefix()) ) {
                continue;
            }

            if ( ch.isRestricted() && !oplayer.hasPermission("kingdom.chat.channel." + ch.getName().toLowerCase())) {
                continue;
            }

            return ch;
        }
        return null;
    }

    @Override
    public void send(OnlinePlayer oplayer, ChatChannel channel, String message) {
        String result = channel.getFormat();
        result = plugin.getPlaceholderManager().handle(oplayer.getPlayer(), result);
        result = result.replace("{message}", message);

        for ( Player recipient : channel.getRecipients() ) {
            OnlinePlayer orecipient = plugin.getIntegration().getOnlinePlayer(recipient);
            orecipient.sendMessage(result);
        }
    }


}
