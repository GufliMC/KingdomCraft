package com.igufguf.kingdomcraft.api.chat;

import com.igufguf.kingdomcraft.api.domain.Player;

import java.util.List;

public interface ChatManager {

    List<ChatChannel> getChatChannels();

    ChatChannel getChatChannel(String name);

    void addChatChannel(ChatChannel chatChannel);

    void removeChatChannel(ChatChannel chatChannel);

    void handle(Player player, String message);

    void send(Player player, ChatChannel channel, String message);

    boolean hasDefaultAccess(Player player, ChatChannel channel);

    boolean canSee(Player player, ChatChannel channel);

    boolean canTalk(Player player, ChatChannel channel);

}
