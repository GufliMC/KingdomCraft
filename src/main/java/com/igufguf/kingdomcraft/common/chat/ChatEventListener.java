package com.igufguf.kingdomcraft.common.chat;

import com.igufguf.kingdomcraft.api.chat.ChatChannel;
import com.igufguf.kingdomcraft.api.chat.ChatManager;
import com.igufguf.kingdomcraft.api.domain.Kingdom;
import com.igufguf.kingdomcraft.api.event.EventListener;

public class ChatEventListener implements EventListener {

    private final ChatManager chatManager;

    public ChatEventListener(ChatManager chatManager) {
        this.chatManager = chatManager;
    }

    @Override
    public void onKingdomCreate(Kingdom kingdom) {
        KingdomChatChannel ch = new KingdomChatChannel(kingdom.getName(), kingdom);
        ch.setFormat("{player} >> {message}");
        chatManager.addChatChannel(ch);
    }

    @Override
    public void onKingdomDelete(Kingdom kingdom) {
        ChatChannel ch = chatManager.getChatChannel(kingdom.getName());
        chatManager.removeChatChannel(ch);
    }

}
