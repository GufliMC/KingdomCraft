package com.guflan.kingdomcraft.api.domain.models;

import java.util.List;
import java.util.Set;

public interface Kingdom {

    String getName();

    String getDisplay();

    void setDisplay(String display);

    String getPrefix();

    void setPrefix(String prefix);

    String getSuffix();

    void setSuffix(String suffix);

    boolean isInviteOnly();

    void setInviteOnly(boolean inviteOnly);

    int getMaxMembers();

    void setMaxMembers(int maxMembers);

    Rank getDefaultRank();

    void setDefaultRank(Rank defaultRank);

    List<Rank> getRanks();

    Rank getRank(String name);

    Rank createRank(String name);

    void deleteRank(Rank rank);

}
