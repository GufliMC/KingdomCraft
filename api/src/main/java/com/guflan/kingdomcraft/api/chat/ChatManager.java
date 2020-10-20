/*
 * This file is part of KingdomCraft.
 *
 * KingdomCraft is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * KingdomCraft is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with KingdomCraft. If not, see <https://www.gnu.org/licenses/>.
 */

package com.guflan.kingdomcraft.api.chat;

import com.guflan.kingdomcraft.api.entity.PlatformPlayer;

import java.util.List;

public interface ChatManager {

    List<ChatChannelFactory> getChatChannelFactories();

    void addChatChannelFactory(ChatChannelFactory factory);

    void removeChatChannelFactory(ChatChannelFactory factory);

    ChatChannelFactory getChatChannelFactory(String name);

    //

    List<ChatChannel> getChatChannels();

    ChatChannel getChatChannel(String name);

    void addChatChannel(ChatChannel chatChannel);

    void removeChatChannel(ChatChannel chatChannel);

    void setDefaultChatChannel(ChatChannel chatChannel);

    ChatChannel getDefaultChatChannel();

    //
    boolean canAccess(PlatformPlayer player, ChatChannel chatChannel);

    boolean canSee(PlatformPlayer player, ChatChannel chatChannel);
}
