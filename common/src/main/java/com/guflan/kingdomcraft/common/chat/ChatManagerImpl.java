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

package com.guflan.kingdomcraft.common.chat;

import com.guflan.kingdomcraft.api.KingdomCraft;
import com.guflan.kingdomcraft.api.chat.ChatChannel;
import com.guflan.kingdomcraft.api.chat.ChatChannelFactory;
import com.guflan.kingdomcraft.api.chat.ChatManager;
import com.guflan.kingdomcraft.api.domain.Kingdom;
import com.guflan.kingdomcraft.api.domain.User;
import com.guflan.kingdomcraft.api.entity.PlatformPlayer;
import com.guflan.kingdomcraft.common.chat.channels.KingdomChatChannel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ChatManagerImpl implements ChatManager {

    private final KingdomCraft kdc;

    private final List<ChatChannelFactory> factories = new ArrayList<>();
    private final List<ChatChannel> chatChannels = new ArrayList<>();

    ChatChannel defaultChannel;

    public ChatManagerImpl(KingdomCraft kdc) {
        this.kdc = kdc;
        kdc.getEventManager().addListener(new ChatEventListener(this));
    }

    @Override
    public List<ChatChannelFactory> getChatChannelFactories() {
        return factories;
    }

    @Override
    public void addChatChannelFactory(ChatChannelFactory factory) {
        if ( factory == null ) {
            return;
        }

        if ( this.factories.contains(factory) ) {
            return;
        }

        this.factories.add(factory);

        for (Kingdom kd : kdc.getKingdoms() ) {
            if ( !factory.doesTarget(kd) ) continue;
            addChatChannel(factory.create(kd));
        }
    }

    @Override
    public void removeChatChannelFactory(ChatChannelFactory factory) {
        this.factories.remove(factory);
    }

    @Override
    public ChatChannelFactory getChatChannelFactory(String name) {
        return factories.stream().filter(c -> c.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    @Override
    public List<ChatChannel> getChatChannels() {
        return chatChannels;
    }

    @Override
    public ChatChannel getChatChannel(String name) {
        return chatChannels.stream().filter(c -> c.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    @Override
    public void addChatChannel(ChatChannel chatChannel) {
        if ( chatChannel == null ) {
            return;
        }

        if ( !this.chatChannels.contains(chatChannel) ) {
            this.chatChannels.add(chatChannel);
        }
    }

    @Override
    public void removeChatChannel(ChatChannel chatChannel) {
        this.chatChannels.remove(chatChannel);
    }

    @Override
    public void setDefaultChatChannel(ChatChannel chatChannel) {
        this.defaultChannel = chatChannel;
    }

    @Override
    public ChatChannel getDefaultChatChannel() {
        return defaultChannel;
    }

    //

    List<ChatChannel> getKingdomChannels(Kingdom kingdom) {
        return getChatChannels().stream().filter(ch -> ch instanceof KingdomChatChannel).filter(ch -> ((KingdomChatChannel) ch).getKingdoms().contains(kingdom)).collect(Collectors.toList());
    }

    List<ChatChannel> getPublicChannels() {
        return getChatChannels().stream().filter(ch -> !(ch instanceof KingdomChatChannel)).collect(Collectors.toList());
    }

    List<ChatChannel> getVisibleChannels(PlatformPlayer player) {
        return getChatChannels().stream().filter(ch -> isVisible(player, ch)).collect(Collectors.toList());
    }

    boolean isVisible(PlatformPlayer player, ChatChannel channel) {
//        if ( player.isAdmin() ) {
//            return true;
//        }

        User user = kdc.getUser(player);
        if ( channel instanceof KingdomChatChannel) {
            KingdomChatChannel ch = (KingdomChatChannel) channel;
            if ( !ch.getKingdoms().contains(user.getKingdom()) ) {
                return false;
            }
        }

        if ( channel.isRestricted() && !player.hasPermission(channel.getPermission())) {
            return false;
        }

        // TODO if channel is toggled off -> return false

        return true;
    }

}
