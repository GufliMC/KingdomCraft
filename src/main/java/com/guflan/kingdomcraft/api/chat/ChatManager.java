package com.guflan.kingdomcraft.api.chat;

import com.guflan.kingdomcraft.api.domain.Kingdom;
import com.guflan.kingdomcraft.api.domain.Player;
import com.guflan.kingdomcraft.api.entity.EntityPlayer;

import java.util.List;

public interface ChatManager {

    List<ChatChannel> getChatChannels();

    ChatChannel getChatChannel(String name);

    void addChatChannel(ChatChannel chatChannel);

    void removeChatChannel(ChatChannel chatChannel);

    List<ChatChannel> getKingdomChannels(Kingdom kingdom);

    List<ChatChannel> getPublicChannels();

    List<ChatChannel> getVisibleChannels(EntityPlayer player);

    boolean isVisible(EntityPlayer player, ChatChannel channel);

    void handle(EntityPlayer player, String message);

    void send(EntityPlayer player, ChatChannel channel, String message);


}
