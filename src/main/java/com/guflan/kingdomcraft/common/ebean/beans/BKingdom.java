package com.guflan.kingdomcraft.common.ebean.beans;

import com.guflan.kingdomcraft.api.domain.Kingdom;
import com.guflan.kingdomcraft.api.domain.User;
import com.guflan.kingdomcraft.api.domain.Rank;
import com.guflan.kingdomcraft.api.domain.Relation;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "kingdoms")
public class BKingdom extends BaseModel implements Kingdom {

    @Column(unique=true)
    final String name;

    String display;
    String prefix;
    String suffix;
    boolean inviteOnly;
    int maxMembers;

    String spawn;

    @OneToOne
    BRank defaultRank;

    @OneToMany(mappedBy = "kingdom")
    List<BRank> ranks;

    @OneToMany(mappedBy = "kingdom")
    Set<BRelation> relations;

    @OneToMany(mappedBy = "kingdom")
    private List<User> members;

    public BKingdom(String name) {
        this.name = name;
    }

    // interface

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
        return prefix != null ? prefix : "";
    }

    @Override
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public String getSuffix() {
        return suffix != null ? suffix : "";
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
        this.defaultRank = (BRank) defaultRank;
    }

    @Override
    public List<Rank> getRanks() {
        return new ArrayList<>(ranks);
    }

    @Override
    public Map<Kingdom, Relation> getRelations() {
        Map<Kingdom, Relation> relationMap = new HashMap<>();
        for ( BRelation rel : relations ) {
            relationMap.put(rel.target, Relation.fromId(rel.relation));
        }
        return relationMap;
    }

    @Override
    public Relation getRelation(Kingdom kingdom) {
        for ( BRelation rel : relations ) {
            if ( rel.target == kingdom ) {
                return Relation.fromId(rel.relation);
            }
        }
        return Relation.NEUTRAL;
    }

    @Override
    public void setRelation(Kingdom kingdom, Relation relation) {
        relations.removeIf(rel -> rel.target == kingdom);
        relations.add(new BRelation(this, (BKingdom) kingdom, relation.getId()));
    }

    @Override
    @Deprecated
    public List<User> getMembers() {
        return new ArrayList<>(members);
    }
}
