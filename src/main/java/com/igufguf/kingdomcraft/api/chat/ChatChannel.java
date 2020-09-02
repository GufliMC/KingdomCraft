package com.igufguf.kingdomcraft.api.chat;

public interface ChatChannel {

    String getName();

    String getDestinationPrefix();

    void setDestionationPrefix(String destionationPrefix);

    String getFormat();

    void setFormat(String format);

    boolean isRestricted();

    boolean isToggleable();

}
