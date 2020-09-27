package com.guflan.kingdomcraft.api.domain;

import java.util.List;
import java.util.Map;

public interface Kingdom {

    String getName();

    String getDisplay();

    void setDisplay(String display);

    String getPrefix();

    void setPrefix(String prefix);

    String getSuffix();

    void setSuffix(String suffix);

    String getSpawn();

    void setSpawn(String spawn);

    boolean isInviteOnly();

    void setInviteOnly(boolean inviteOnly);

    int getMaxMembers();

    void setMaxMembers(int maxMembers);

    Rank getDefaultRank();

    void setDefaultRank(Rank defaultRank);

    List<Rank> getRanks();

    Map<Kingdom, Relation> getRelations();

    Relation getRelation(Kingdom kingdom);

    void setRelation(Kingdom kingdom, Relation relation);

    List<Player> getMembers();

}
