package com.igufguf.kingdomcraft.common.chat;

import com.igufguf.kingdomcraft.api.chat.ChatChannel;

public class DefaultChatChannel implements ChatChannel {

    private final String name;

    private String prefix;
    private String format;

    private boolean restricted;
    private boolean toggleable;

    private int range;

    public DefaultChatChannel(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getPrefix() {
        return prefix;
    }

    @Override
    public void setPrefix(String prefix) {
        this.prefix = prefix;
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
        return restricted;
    }

    @Override
    public void setRestricted(boolean restricted) {
        this.restricted = restricted;
    }

    @Override
    public String getPermission() {
        return "kingdom.chat.channel." + name;
    }

    @Override
    public boolean isToggleable() {
        return toggleable;
    }

    @Override
    public void setToggleable(boolean toggleable) {
        this.toggleable = toggleable;
    }

    @Override
    public int getRange() {
        return range;
    }

    @Override
    public void setRange(int range) {
        this.range = range;
    }

}
