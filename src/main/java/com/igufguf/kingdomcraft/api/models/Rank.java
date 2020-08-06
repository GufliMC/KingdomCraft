package com.igufguf.kingdomcraft.api.models;

public interface Rank {


    public String getName();

    public Kingdom getKingdom();

    public String getDisplay();

    public void setDisplay(String display);

    public String getPrefix();

    public void setPrefix(String prefix);

    public String getSuffix();

    public void setSuffix(String suffix);
}
