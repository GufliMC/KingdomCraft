package com.guflan.kingdomcraft.api.chat;

import com.guflan.kingdomcraft.api.domain.Kingdom;
import com.guflan.kingdomcraft.api.entity.Player;

import java.util.List;

public interface ChatManager {

    List<ChatChannel> getChatChannels();

    ChatChannel getChatChannel(String name);

    void addChatChannel(ChatChannel chatChannel);

    void removeChatChannel(ChatChannel chatChannel);

    List<ChatChannel> getKingdomChannels(Kingdom kingdom);

    List<ChatChannel> getPublicChannels();

    List<ChatChannel> getVisibleChannels(Player player);

    boolean isVisible(Player player, ChatChannel channel);

    void handle(Player player, String message);

    void send(Player player, ChatChannel channel, String message);


}
