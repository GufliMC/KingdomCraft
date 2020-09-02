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
    public void onKingdomDelete(Kingdom kingdom) {
        chatManager.getKingdomChannels(kingdom).forEach(chatManager::removeChatChannel);
    }

}
