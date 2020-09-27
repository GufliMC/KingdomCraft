package com.guflan.kingdomcraft.api.chat;

public interface ChatChannel {

    String getName();

    String getPrefix();

    void setPrefix(String prefix);

    String getFormat();

    void setFormat(String format);

    boolean isRestricted();

    void setRestricted(boolean restricted);

    String getPermission();

    boolean isToggleable();

    void setToggleable(boolean toggleable);

    int getRange();

    void setRange(int range);

}
