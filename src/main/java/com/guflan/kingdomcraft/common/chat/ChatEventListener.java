package com.guflan.kingdomcraft.common.chat;

import com.guflan.kingdomcraft.api.chat.ChatManager;
import com.guflan.kingdomcraft.api.domain.Kingdom;
import com.guflan.kingdomcraft.api.event.EventListener;

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
