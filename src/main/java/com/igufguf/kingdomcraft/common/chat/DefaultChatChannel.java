package com.igufguf.kingdomcraft.common.chat;

import com.igufguf.kingdomcraft.api.chat.ChatChannel;
import com.igufguf.kingdomcraft.api.domain.Player;

import java.util.List;

public class DefaultChatChannel implements ChatChannel {

    private final String name;

    private String destinationPrefix;
    private String format;

    public DefaultChatChannel(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDestinationPrefix() {
        return destinationPrefix;
    }

    @Override
    public void setDestionationPrefix(String destionationPrefix) {
        this.destinationPrefix = destionationPrefix;
    }

    @Override
    public String getFormat() {
        return format;
    }

    @Override
    public void setFormat(String format) {
        this.format = format;
    }

    @Override
    public boolean isRestricted() {
        return false;
    }

    @Override
    public boolean isToggleable() {
        return false;
    }

    @Override
    public List<Player> getRecipients() {
        return null;
    }

}
