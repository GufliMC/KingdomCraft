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
import com.gufli.kingdomcraft.api.events.KingdomCreateEvent;
import com.gufli.kingdomcraft.api.events.KingdomDeleteEvent;
import com.gufli.kingdomcraft.api.events.PlayerChatEvent;
import com.gufli.kingdomcraft.api.events.PlayerLoadedEvent;
import com.gufli.kingdomcraft.common.KingdomCraftImpl;
import com.gufli.kingdomcraft.common.chat.channels.BasicChatChannel;
import com.gufli.kingdomcraft.common.chat.channels.KingdomChatChannel;
import com.gufli.kingdomcraft.common.config.Configuration;
import org.apache.commons.text.StringEscapeUtils;

import java.text.DecimalFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ChatManagerImpl implements ChatManager {

    private final KingdomCraftImpl kdc;

    private final List<ChatChannelFactory> factories = new ArrayList<>();
    private final List<ChatChannel> chatChannels = new ArrayList<>();

    private ChatChannel defaultChannel;

    private boolean enabled;

    public ChatManagerImpl(KingdomCraftImpl kdc) {
        this.kdc = kdc;

        // disable social spy on join if permissions are removed
        kdc.getEventManager().addListener(PlayerLoadedEvent.class, (e) -> {
            if ( !e.getPlayer().isSocialSpyEnabled() ) {
                return;
            }

            if ( !e.getPlayer().hasPermission("kingdom.socialspy") ) {
                e.getPlayer().setSocialSpyEnabled(false);
                kdc.saveAsync(e.getPlayer().getUser());
                return;
            }

            kdc.getMessages().send(e.getPlayer(), "socialSpyJoin");
        });

        // update channels on kingdom delete
        kdc.getEventManager().addListener(KingdomDeleteEvent.class, (e) -> {
            getKingdomChannels(e.getKingdom()).forEach(ch -> {
                KingdomChatChannel kch = ((KingdomChatChannel) ch);
                kch.getKingdoms().remove(e.getKingdom());
                if ( kch.getKingdoms().isEmpty() ) {
                    removeChatChannel(kch);
                }
            });
        });

        // update channels on kingdom create
        kdc.getEventManager().addListener(KingdomCreateEvent.class, (e) -> {
            for (ChatChannelFactory f : factories ) {
                if ( !f.shouldCreate(e.getKingdom()) ) continue;
                addChatChannel(f.create(e.getKingdom()));
            }
        });
    }

    public boolean isEnabled() {
        return enabled;
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

        if (user == null ) {
            return false; // player is not fully loaded because chat thread is async
        }

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

    List<ChatChannel> getKingdomChannels(Kingdom kingdom) {
        return getChatChannels().stream().filter(ch -> ch instanceof KingdomChatChannel)
                .filter(ch -> ((KingdomChatChannel) ch).getKingdoms().contains(kingdom)).collect(Collectors.toList());
    }

    @Override
    public void dispatch(PlatformPlayer player, String message) {
        List<ChatChannel> channels = getChatChannels().stream()
                .filter(ChatChannel::isEnabled)
                .filter(c -> canTalk(player, c))
                .sorted(Comparator.comparingInt(ch -> ch.getPrefix() == null ? 0 : -ch.getPrefix().length()))
                .collect(Collectors.toList());

        String strippedMessage = kdc.getPlugin().decolorify(message);

        ChatChannel channel = null;
        if (player.has("DEFAULT_CHATCHANNEL") ) {
            channel = channels.stream().filter(ch -> ch.getName().equals(player.get("DEFAULT_CHATCHANNEL", String.class)))
                    .findFirst().orElse(null);
        }

        if ( player.hasPermission("kingdom.defaultchatchannel") ) {
            // unset default chat channel by just typing the prefix
            if (channel != null && channel.getPrefix() != null && channel.getPrefix().equals(strippedMessage)) {
                player.remove("DEFAULT_CHATCHANNEL");
                kdc.getMessages().send(player, "cmdDefaultChatChannelDisable");
                return;
            }
            // set default chat channel by just typing the prefix
            else {
                for (ChatChannel ch : channels) {
                    if (ch.getPrefix() != null && !ch.getPrefix().equals("") && ch.getPrefix().equals(strippedMessage)) {
                        player.set("DEFAULT_CHATCHANNEL", ch.getName());
                        kdc.getMessages().send(player, "cmdDefaultChatChannel", ch.getName());
                        return;
                    }
                }
            }
        }

        for ( ChatChannel ch : channels ) {
            if ( ch == channel ) {
                continue;
            }

            if ( ch.getPrefix() != null && !strippedMessage.startsWith(ch.getPrefix()) ) {
                continue;
            }

            // If the player is using a default channel, use that prefix instead to talk in the channel without prefix
            if ( channel != null && channel.getPrefix() != null && ch.getPrefix() != null && ch.getPrefix().equals("") ) {
                if ( strippedMessage.startsWith(channel.getPrefix()) ) {
                    message = message.replaceFirst(Pattern.quote(channel.getPrefix()), "");
                    channel = ch;
                    break;
                }
                continue;
            }

            if ( ch.getPrefix() != null ) {
                message = message.replaceFirst(Pattern.quote(ch.getPrefix()), "");
            }
            channel = ch;
            break;
        }

        if ( channel == null ) {
            if (getDefaultChatChannel() == null) {
                kdc.getMessages().send(player, "chatNoChannel");
                return;
            }

            channel = getDefaultChatChannel();
        }

        // Chat cooldown check
        String cooldownKey = "CHAT_COOLDOWN_" + channel.getName();
        if ( channel.getCooldown() > 0 && player.has(cooldownKey)
                && !player.hasPermission("kingdom.chat.bypass.cooldown")) {

            long lastMessage = player.get(cooldownKey, Long.class);
            long diff = System.currentTimeMillis() - lastMessage;
            if ( diff < channel.getCooldown() * 1000L) {
                float remaining = ((channel.getCooldown() * 1000) - diff) / 1000f;
                DecimalFormat df = new DecimalFormat("0.0");
                kdc.getMessages().send(player, "chatChannelCooldown", df.format(remaining));
                return;
            }
        }

        if ( kdc.getPlugin().decolorify(message).equals("") ) {
            return;
        }

        dispatch(player, channel, message);
    }

    @Override
    public void dispatch(PlatformPlayer player, ChatChannel channel, String message) {
        List<PlatformPlayer> receivers = kdc.getOnlinePlayers().stream()
                .filter(p -> canRead(p, channel))
                .filter(p -> channel.getRange() <= 0 || p.getLocation().distanceTo(player.getLocation()) <= channel.getRange())
                .collect(Collectors.toList());

        PlayerChatEvent event = new PlayerChatEvent(player, channel, message, channel.getFormat(), receivers);
        kdc.getEventManager().dispatch(event);

        if ( event.isCancelled() ) {
            return;
        }

        message = event.getMessage();
        if ( message == null ) {
            return;
        }

        String result = event.getFormat();
        result = StringEscapeUtils.unescapeJava(result);
        result = kdc.getPlaceholderManager().handle(player, result);
        result = kdc.getMessages().colorify(result);

        if ( player.hasPermission("kingdom.chat.colors") ) {
            message = kdc.getMessages().colorify(message);
        }

        result = kdc.getPlaceholderManager().strip(result, "message", "player");
        result = result.replace("{message}", message);
        result = result.replace("{player}", player.getName());

        List<PlatformPlayer> finalReceivers = event.getReceivers();
        if ( finalReceivers == null || finalReceivers.isEmpty() ) {
            return;
        }

        if ( channel.getCooldown() > 0 && !player.hasPermission("kingdom.chat.bypass.cooldown") ) {
            player.set("CHAT_COOLDOWN_" + channel.getName(), System.currentTimeMillis());
        }

        if ( !receivers.contains(player) ) {
            receivers.add(player);
        }

        String finalResult = result;
        for ( PlatformPlayer p : receivers ) {
            p.sendMessage(finalResult);
        }

        String ss = kdc.getMessages().getMessage("socialSpyPrefix", channel.getName()) + finalResult;
        kdc.getOnlinePlayers().stream()
                .filter(PlatformPlayer::isSocialSpyEnabled)
                .filter(p -> !receivers.contains(p))
                .forEach(p -> p.sendMessage(ss));

        kdc.getPlugin().log("[" + channel.getName() + "] " + player.getName() + ": " + message);
    }

    // SETUP

    public void load(Configuration config) {
        enabled = config.contains("enabled") && config.getBoolean("enabled");

        if ( !enabled ) {
            return;
        }

        if ( !config.contains("channels") || config.getConfigurationSection("channels") == null ) {
            return;
        }

        factories.clear();
        chatChannels.clear();

        String defaultchannel = config.getString("default-channel");

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
                continue;
            }

            ChatChannel ch = new BasicChatChannel(name);
            setup(ch, cs);
            addChatChannel(ch);
        }

        if ( defaultchannel != null ) {
            ChatChannel ch = getChatChannel(defaultchannel);
            if ( ch != null ) {
                setDefaultChatChannel(ch);
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
