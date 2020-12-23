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

import com.gufli.kingdomcraft.api.chat.ChatChannel;
import com.gufli.kingdomcraft.api.chat.ChatChannelFactory;
import com.gufli.kingdomcraft.api.chat.ChatManager;
import com.gufli.kingdomcraft.api.domain.Kingdom;
import com.gufli.kingdomcraft.api.domain.User;
import com.gufli.kingdomcraft.api.domain.UserChatChannel;
import com.gufli.kingdomcraft.api.entity.PlatformPlayer;
import com.gufli.kingdomcraft.common.KingdomCraftImpl;
import com.gufli.kingdomcraft.common.chat.channels.BasicChatChannel;
import com.gufli.kingdomcraft.common.chat.channels.KingdomChatChannel;
import com.gufli.kingdomcraft.common.config.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class ChatManagerImpl implements ChatManager {

    private final KingdomCraftImpl kdc;

    final List<ChatChannelFactory> factories = new ArrayList<>();
    final List<ChatChannel> chatChannels = new ArrayList<>();

    ChatChannel defaultChannel;

    public ChatManagerImpl(KingdomCraftImpl kdc, Configuration config) {
        this.kdc = kdc;
        kdc.getEventManager().addListener(new ChatEventListener(this));
        reload(config);
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

    private boolean canAccess(PlatformPlayer player, ChatChannel channel) {
        User user = kdc.getUser(player);
        if ( channel instanceof KingdomChatChannel) {
            KingdomChatChannel ch = (KingdomChatChannel) channel;
            if ( !ch.getKingdoms().contains(user.getKingdom()) ) {
                return false;
            }
        }

        if ( channel.getRestrictMode() == ChatChannel.RestrictMode.READ
                && !player.hasPermission(channel.getPermission())) {
            return false;
        }

        return true;
    }

    @Override
    public boolean canTalk(PlatformPlayer player, ChatChannel channel) {
        if ( !canAccess(player, channel) ) {
            return false;
        }

        if ( channel.getRestrictMode() == ChatChannel.RestrictMode.TALK
                && !player.hasPermission(channel.getPermission())) {
            return false;
        }

        return true;
    }

    @Override
    public boolean canRead(PlatformPlayer player, ChatChannel channel) {
        if ( !canAccess(player, channel) ) {
            return false;
        }

        User user = kdc.getUser(player);
        if ( channel.isToggleable() ) {
            UserChatChannel ucc = user.getChatChannel(channel.getName());
            if ( ucc != null && !ucc.isEnabled() ) {
                return false;
            }
        }

        return true;
    }

    //

    List<ChatChannel> getKingdomChannels(Kingdom kingdom) {
        return getChatChannels().stream().filter(ch -> ch instanceof KingdomChatChannel)
                .filter(ch -> ((KingdomChatChannel) ch).getKingdoms().contains(kingdom)).collect(Collectors.toList());
    }

    public void reload(Configuration config) {
        if ( !config.contains("enabled") || !config.getBoolean("enabled") ) {
            return;
        }

        if ( !config.contains("channels") || config.getConfigurationSection("channels") == null ) {
            return;
        }

        factories.clear();
        chatChannels.clear();

        Configuration channels = config.getConfigurationSection("channels");
        for ( String name : channels.getKeys(false) ) {
            Configuration cs = channels.getConfigurationSection(name);

            if ( !cs.contains("format") ) {
                kdc.getPlugin().log("Cannot create channel with name '" + name + "' because no format is given.", Level.WARNING);
                continue;
            }

            if ( cs.contains("kingdoms") ) {
                ChatChannelFactory factory = createFactory(name, cs);
                addFactory(factory);
            } else {
                ChatChannel ch = new BasicChatChannel(name);
                setup(ch, cs);
                addChatChannel(ch);

                if ( cs.contains("default") && cs.getBoolean("default") ) {
                    setDefaultChatChannel(ch);
                }
            }
        }
    }

    private void addFactory(ChatChannelFactory factory) {
        factories.add(factory);
        for (Kingdom kd : kdc.getKingdoms() ) {
            if ( factory.shouldCreate(kd) ) {
                addChatChannel(factory.create(kd));
            }
        }
    }

    private void setup(ChatChannel channel, Configuration section) {
        channel.setFormat(section.getString("format"));

        if ( section.contains("prefix") ) {
            channel.setPrefix(section.getString("prefix"));
        }

        if ( section.contains("toggleable") ) {
            channel.setToggleable(section.getBoolean("toggleable"));
        }

        if ( section.contains("restrict") ) {
            if ( section.get("restrict") instanceof Boolean ) {
                boolean val = section.getBoolean("restrict");
                if ( val ) {
                    channel.setRestrictMode(ChatChannel.RestrictMode.READ);
                } else {
                    channel.setRestrictMode(ChatChannel.RestrictMode.NONE);
                }
            } else {
                ChatChannel.RestrictMode mode = ChatChannel.RestrictMode.get(section.getString("restrict"));
                if ( mode == null ) {
                    channel.setRestrictMode(ChatChannel.RestrictMode.NONE);
                } else {
                    channel.setRestrictMode(mode);
                }
            }
        }

        if ( section.contains("range") ) {
            channel.setRange(section.getInt("range"));
        }

        if ( section.contains("cooldown") ) {
            channel.setCooldown(section.getInt("cooldown"));
        }
    }

    private ChatChannelFactory createFactory(String name, Configuration section) {
        boolean allKingdoms = false;
        List<String> kingdoms = new ArrayList<>();
        if ( section.get("kingdoms") instanceof String ) {
            if ( section.getString("kingdoms").equals("*") ) {
                allKingdoms = true;
            } else {
                kingdoms.add(section.getString("kingdoms"));
            }
        } else {
            kingdoms.addAll(section.getStringList("kingdoms"));
        }

        final boolean finalAllKingdoms = allKingdoms;
        return new ChatChannelFactory() {
            @Override
            public boolean shouldCreate(Kingdom kingdom) {
                return finalAllKingdoms || kingdoms.contains(kingdom.getName().toLowerCase());
            }

            @Override
            public String getName() {
                return name;
            }

            @Override
            public ChatChannel create(Kingdom kingdom) {
                if ( !finalAllKingdoms && kingdoms.size() > 1 ) {
                    KingdomChatChannel ch = (KingdomChatChannel) getChatChannel(getName());
                    if ( ch == null ) {
                        ch = new KingdomChatChannel(getName(), kingdom);
                        setup(ch, section);
                    } else {
                        ch.getKingdoms().add(kingdom);
                    }
                    return ch;
                }

                ChatChannel ch = new KingdomChatChannel(getName() + "-" + kingdom.getName(), kingdom);
                setup(ch, section);
                return ch;
            }
        };
    }

}
