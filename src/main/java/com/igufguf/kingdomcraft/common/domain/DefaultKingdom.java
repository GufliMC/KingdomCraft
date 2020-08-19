package com.igufguf.kingdomcraft.common.domain;

import com.igufguf.kingdomcraft.api.domain.Kingdom;
import com.igufguf.kingdomcraft.api.domain.Player;
import com.igufguf.kingdomcraft.api.domain.Rank;
import com.igufguf.kingdomcraft.api.domain.Relation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DefaultKingdom implements Kingdom {

    private final String name;

    private String display;
    private String prefix;
    private String suffix;

    private String spawn;
    private boolean inviteOnly;
    private int maxMembers;

    private Rank defaultRank;
    private List<Rank> ranks = new ArrayList<>();

    private Map<Kingdom, Relation> relations;

    private List<Player> members;

    public DefaultKingdom(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
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
    public String getSpawn() {
        return spawn;
    }

    @Override
    public void setSpawn(String spawn) {
        this.spawn = spawn;
    }

    @Override
    public boolean isInviteOnly() {
        return inviteOnly;
    }

    @Override
    public void setInviteOnly(boolean inviteOnly) {
        this.inviteOnly = inviteOnly;
    }

    @Override
    public int getMaxMembers() {
        return maxMembers;
    }

    @Override
    public void setMaxMembers(int maxMembers) {
        this.maxMembers = maxMembers;
    }

    @Override
    public Rank getDefaultRank() {
        return defaultRank;
    }

    @Override
    public void setDefaultRank(Rank defaultRank) {
        if ( !this.ranks.contains(defaultRank) ) {
            throw new IllegalArgumentException("The given defaultRank does not belong to this kingdom.");
        }
        this.defaultRank = defaultRank;
    }

    @Override
    public List<Rank> getRanks() {
        return ranks;
    }

    @Override
    public Map<Kingdom, Relation> getRelations() {
        return relations;
    }

    @Override
    public Relation getRelation(Kingdom kingdom) {
        return relations.getOrDefault(kingdom, Relation.NEUTRAL);
    }

    @Override
    public void setRelation(Kingdom kingdom, Relation relation) {
        relations.put(kingdom, relation);
    }

    @Override
    @Deprecated
    public List<Player> getMembers() {
        return members;
    }
}
