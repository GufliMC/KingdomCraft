package com.igufguf.kingdomcraft.common.kingdom;

import com.igufguf.kingdomcraft.api.domain.Kingdom;
import com.igufguf.kingdomcraft.api.domain.Rank;

public class DefaultRank implements Rank {

    private final String name;
    private final Kingdom kingdom;

    private String display;
    private String prefix;
    private String suffix;
    private int maxMembers;

    public DefaultRank(String name, Kingdom kingdom) {
        this.name = name;
        this.kingdom = kingdom;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Kingdom getKingdom() {
        return kingdom;
    }

    @Override
    public String getDisplay() {
        return display != null ? display : name;
    }

    @Override
    public void setDisplay(String display) {
        this.display = display;
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
    public String getSuffix() {
        return suffix;
    }

    @Override
    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    @Override
    public int getMaxMembers() {
        return maxMembers;
    }

    @Override
    public void setMaxMembers(int maxMembers) {
        this.maxMembers = maxMembers;
    }
}
