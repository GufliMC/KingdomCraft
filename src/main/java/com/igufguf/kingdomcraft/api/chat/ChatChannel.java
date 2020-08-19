package com.igufguf.kingdomcraft.api.chat;

import com.igufguf.kingdomcraft.api.domain.Player;

import java.util.List;

public interface ChatChannel {

    String getName();

    String getDestinationPrefix();

    void setDestionationPrefix(String destionationPrefix);

    String getFormat();

    void setFormat(String format);

    boolean isRestricted();

    boolean isToggleable();

    List<Player> getRecipients();

}
