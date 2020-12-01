/*
 * This file is part of KingdomCraft.
 *
 * KingdomCraft is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * KingdomCraft is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with KingdomCraft. If not, see <https://www.gnu.org/licenses/>.
 */

package com.gufli.kingdomcraft.common.chat;

import com.gufli.kingdomcraft.api.chat.ChatChannelFactory;
import com.gufli.kingdomcraft.api.domain.Kingdom;
import com.gufli.kingdomcraft.api.event.EventListener;
import com.gufli.kingdomcraft.common.chat.channels.KingdomChatChannel;

public class ChatEventListener implements EventListener {

    private final ChatManagerImpl chatManager;

    public ChatEventListener(ChatManagerImpl chatManager) {
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
        for (ChatChannelFactory f : chatManager.factories ) {
            if ( !f.shouldCreate(kingdom) ) continue;
            chatManager.addChatChannel(f.create(kingdom));
        }
    }

}
