package com.igufguf.kingdomcraft.api.models;

import java.util.List;

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

    public void addRank(Rank rank);

    public void removeRank(Rank rank);


}
