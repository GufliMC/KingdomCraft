package com.guflan.kingdomcraft.common.chat;

import com.guflan.kingdomcraft.api.chat.ChatChannelBlueprint;
import com.guflan.kingdomcraft.api.chat.ChatManager;
import com.guflan.kingdomcraft.api.domain.models.Kingdom;
import com.guflan.kingdomcraft.api.event.EventListener;
import com.guflan.kingdomcraft.common.chat.channels.KingdomChatChannel;

public class ChatEventListener implements EventListener {

    private final ChatManager chatManager;

    public ChatEventListener(ChatManager chatManager) {
        this.chatManager = chatManager;
    }

    @Override
    public void onKingdomDelete(Kingdom kingdom) {
        chatManager.getKingdomChannels(kingdom).forEach(ch -> {
            KingdomChatChannel kch = ((KingdomChatChannel) ch);
            kch.getKingdoms().remove(kingdom);
            if ( kch.getKingdoms().isEmpty() ) {
                chatManager.removeChatChannel(kch);
            }
        });
    }

    @Override
    public void onKingdomCreate(Kingdom kingdom) {
        for (ChatChannelBlueprint bp : chatManager.getBlueprints() ) {
            if ( !bp.doesTarget(kingdom) ) continue;
            chatManager.addChatChannel(bp.create(kingdom));
        }
    }

}
