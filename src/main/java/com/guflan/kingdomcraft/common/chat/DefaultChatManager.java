package com.guflan.kingdomcraft.common.chat;

import com.guflan.kingdomcraft.api.KingdomCraftPlugin;
import com.guflan.kingdomcraft.api.chat.ChatChannel;
import com.guflan.kingdomcraft.api.chat.ChatManager;
import com.guflan.kingdomcraft.api.domain.Kingdom;
import com.guflan.kingdomcraft.api.domain.Player;
import com.guflan.kingdomcraft.api.entity.EntityPlayer;

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
        if ( chatChannel == null ) {
            return;
        }

        if ( !this.chatChannels.contains(chatChannel) ) {
            this.chatChannels.add(chatChannel);
        }
    }

    @Override
    public void removeChatChannel(ChatChannel chatChannel) {
        this.chatChannels.remove(chatChannel);
    }

    @Override
    public List<ChatChannel> getKingdomChannels(Kingdom kingdom) {
        return getChatChannels().stream().filter(ch -> ch instanceof KingdomChatChannel).filter(ch -> ((KingdomChatChannel) ch).getKingdom() == kingdom).collect(Collectors.toList());
    }

    @Override
    public List<ChatChannel> getPublicChannels() {
        return getChatChannels().stream().filter(ch -> !(ch instanceof KingdomChatChannel)).collect(Collectors.toList());
    }

    @Override
    public List<ChatChannel> getVisibleChannels(EntityPlayer player) {
        return getChatChannels().stream().filter(ch -> isVisible(player, ch)).collect(Collectors.toList());
    }

    @Override
    public boolean isVisible(EntityPlayer player, ChatChannel channel) {
        if ( player.isAdmin() ) {
            return true;
        }

        if ( channel instanceof KingdomChatChannel ) {
            KingdomChatChannel ch = (KingdomChatChannel) channel;
            if ( player.getPlayer().getKingdom() != ch.getKingdom() ) {
                return false;
            }
        }

        if ( channel.isRestricted() && !player.hasPermission(channel.getPermission())) {
            return false;
        }

        // TODO if channel is toggled off -> return false

        return true;
    }

    @Override
    public void handle(EntityPlayer player, String message) {

        List<ChatChannel> channels = getVisibleChannels(player);
        channels.sort(Comparator.comparingInt(ch -> -ch.getPrefix().length()));

        ChatChannel channel = null;
        for ( ChatChannel ch : channels ) {
            if ( ch.getPrefix() != null && !message.startsWith(ch.getPrefix()) ) {
                continue;
            }
            channel = ch;
            break;
        }

        if ( channel == null ) {
            // TODO idk
            return;
        }

        if ( channel.getPrefix() != null ) {
            message = message.substring(channel.getPrefix().length()).trim();
        }

        send(player, channel, message);
    }

    @Override
    public void send(EntityPlayer player, ChatChannel channel, String message) {
        String result = channel.getFormat();
        result = plugin.getPlaceholderManager().handle(player.getPlayer(), result);
        result = plugin.translateColors(result);

        result = result.replace("{message}", plugin.stripColors(message));
        result = result.replace("{player}", player.getPlayer().getName());

        String finalResult = result;
        plugin.getPlayerManager().getOnlinePlayers().stream().filter(p -> isVisible(p, channel)).forEach(p -> p.sendMessage(finalResult));
        System.out.println(plugin.stripColors(result));
    }

}
