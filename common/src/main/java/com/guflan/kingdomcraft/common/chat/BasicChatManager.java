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
import com.guflan.kingdomcraft.api.chat.ChatChannelBlueprint;
import com.guflan.kingdomcraft.api.chat.ChatManager;
import com.guflan.kingdomcraft.api.domain.models.Kingdom;
import com.guflan.kingdomcraft.api.domain.models.User;
import com.guflan.kingdomcraft.api.entity.Player;
import com.guflan.kingdomcraft.common.chat.channels.KingdomChatChannel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class BasicChatManager implements ChatManager {

    private final KingdomCraft kdc;

    private final List<ChatChannelBlueprint> blueprints = new ArrayList<>();
    private final List<ChatChannel> chatChannels = new ArrayList<>();

    private ChatChannel defaultChannel;

    public BasicChatManager(KingdomCraft kdc) {
        this.kdc = kdc;
        kdc.getEventManager().addListener(new ChatEventListener(this));
    }

    @Override
    public List<ChatChannelBlueprint> getBlueprints() {
        return blueprints;
    }

    @Override
    public void addBlueprint(ChatChannelBlueprint blueprint) {
        if ( blueprint == null ) {
            return;
        }

        if ( this.blueprints.contains(blueprint) ) {
            return;
        }

        this.blueprints.add(blueprint);

        for (Kingdom kd : kdc.getKingdoms() ) {
            if ( !blueprint.doesTarget(kd) ) continue;
            addChatChannel(blueprint.create(kd));
        }
    }

    @Override
    public void removeBlueprint(ChatChannelBlueprint blueprint) {
        this.blueprints.remove(blueprint);
    }

    @Override
    public ChatChannelBlueprint getBlueprint(String name) {
        return blueprints.stream().filter(c -> c.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
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
    public List<ChatChannel> getKingdomChannels(Kingdom kingdom) {
        return getChatChannels().stream().filter(ch -> ch instanceof KingdomChatChannel).filter(ch -> ((KingdomChatChannel) ch).getKingdoms().contains(kingdom)).collect(Collectors.toList());
    }

    @Override
    public List<ChatChannel> getPublicChannels() {
        return getChatChannels().stream().filter(ch -> !(ch instanceof KingdomChatChannel)).collect(Collectors.toList());
    }

    @Override
    public List<ChatChannel> getVisibleChannels(Player player) {
        return getChatChannels().stream().filter(ch -> isVisible(player, ch)).collect(Collectors.toList());
    }

    @Override
    public boolean isVisible(Player player, ChatChannel channel) {
//        if ( player.isAdmin() ) {
//            return true;
//        }

        User user = kdc.getUser(player);
        if ( channel instanceof KingdomChatChannel ) {
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

    @Override
    public void handle(Player player, String message) {
        List<ChatChannel> channels = getVisibleChannels(player);
        channels.sort(Comparator.comparingInt(ch -> -ch.getPrefix().length()));

        ChatChannel channel = null;
        for ( ChatChannel ch : channels ) {
            if ( ch.getPrefix() != null && !message.startsWith(ch.getPrefix()) ) {
                continue;
            }
            channel = ch;
            break;
        }

        if ( channel == null ) {
            if ( defaultChannel == null ) {
                kdc.getMessageManager().send(player, "errorInvalidChatSetup");
                return;
            }

            channel = defaultChannel;
        }
        else if ( channel.getPrefix() != null ) {
            message = message.substring(channel.getPrefix().length()).trim();
        }

        send(player, channel, message);
    }

    @Override
    public void send(Player player, ChatChannel channel, String message) {
        String result = channel.getFormat();
        result = kdc.getPlaceholderManager().handle(player, result);
        result = kdc.getMessageManager().colorify(result);

        message = kdc.getMessageManager().colorify(message);
        if ( !player.hasPermission("kingdom.chat.colors") ) {
            message = kdc.getMessageManager().decolorify(message);
        }

        result = result.replace("{message}", message);
        result = result.replace("{player}", player.getName());

        String finalResult = kdc.getPlaceholderManager().strip(result);
        kdc.getOnlinePlayers().stream().filter(p -> isVisible(p, channel)).forEach(p -> p.sendMessage(finalResult));
        System.out.println("[CHAT] " + kdc.getMessageManager().decolorify(finalResult));
    }

}
