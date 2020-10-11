package com.guflan.kingdomcraft.api.domain;

public interface Rank {


    String getName();

    Kingdom getKingdom();

    String getDisplay();

    void setDisplay(String display);

    String getPrefix();

    void setPrefix(String prefix);

    String getSuffix();

    void setSuffix(String suffix);

    int getMaxMembers();

    void setMaxMembers(int maxMembers);

    int getLevel();

    void setLevel(int level);
}
