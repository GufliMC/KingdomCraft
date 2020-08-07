package com.igufguf.kingdomcraft.api.domain;

import java.util.List;
import java.util.Map;

public interface Kingdom {

    String getName();

    public String getDisplay();

    public void setDisplay(String display);

    public String getPrefix();

    public void setPrefix(String prefix);

    public String getSuffix();

    public void setSuffix(String suffix);

    public String getSpawn();

    public void setSpawn(String spawn);

    public boolean isInviteOnly();

    public void setInviteOnly(boolean inviteOnly);

    public Rank getDefaultRank();

    public void setDefaultRank(Rank defaultRank);

    public List<Rank> getRanks();

    public Map<Kingdom, Relation> getRelations();

    public Relation getRelation(Kingdom kingdom);

    public void setRelation(Kingdom kingdom, Relation relation);

    public List<Player> getMembers();

}
