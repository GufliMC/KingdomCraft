package com.igufguf.kingdomcraft.api.chat;

import com.igufguf.kingdomcraft.api.domain.Player;
import com.igufguf.kingdomcraft.api.integration.OnlinePlayer;

import java.util.List;

public interface ChatManager {

    List<ChatChannel> getChatChannels();

    ChatChannel getChatChannel(String name);

    void addChatChannel(ChatChannel chatChannel);

    void removeChatChannel(ChatChannel chatChannel);

    void handle(OnlinePlayer player, String message);

    void send(OnlinePlayer player, ChatChannel channel, String message);

}
