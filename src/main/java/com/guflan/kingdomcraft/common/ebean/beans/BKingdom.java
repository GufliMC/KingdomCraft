package com.guflan.kingdomcraft.common.ebean.beans;

import com.guflan.kingdomcraft.api.domain.Kingdom;
import com.guflan.kingdomcraft.api.domain.User;
import com.guflan.kingdomcraft.api.domain.Rank;
import com.guflan.kingdomcraft.api.domain.Relation;
import io.ebean.Model;
import io.ebean.annotation.WhenCreated;
import io.ebean.annotation.WhenModified;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "kingdoms")
public class BKingdom extends Model implements Kingdom {

    @Id
    public long id;

    @Column(unique=true)
    public String name;

    public String display;
    public String prefix;
    public String suffix;
    public boolean inviteOnly;
    public int maxMembers;

    @OneToOne
    public BRank defaultRank;

    @OneToMany(mappedBy = "kingdom")
    public Set<BRank> ranks;

    @OneToMany(mappedBy = "kingdom")
    public Set<BRelation> relations;

    @OneToMany(mappedBy = "kingdom")
    public Set<BUser> members;

    @WhenCreated
    Date createdAt;

    @WhenModified
    Date updatedAt;

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
    public Rank getRank(String name) {
        return ranks.stream().filter(r -> r.name.equals(name)).findFirst().orElse(null);
    }

    @Override
    public Rank addRank(String name) {
        BRank rank = new BRank();
        rank.name = name;
        rank.kingdom = this;
        ranks.add(rank);
        return rank;
    }

    @Override
    public void deleteRank(Rank rank) {
        ranks.remove(rank);
    }

    @Override
    public Map<Kingdom, Relation> getRelations() {
        Map<Kingdom, Relation> relationMap = new HashMap<>();
        for ( BRelation rel : relations ) {
            relationMap.put(rel.otherKingdom, Relation.fromId(rel.relation));
        }
        return relationMap;
    }

    @Override
    public Relation getRelation(Kingdom kingdom) {
        for ( BRelation rel : relations ) {
            if ( rel.otherKingdom == kingdom ) {
                return Relation.fromId(rel.relation);
            }
        }
        return Relation.NEUTRAL;
    }

    @Override
    public void setRelation(Kingdom kingdom, Relation relation) {
        relations.removeIf(rel -> rel.otherKingdom == kingdom);

        BRelation rel = new BRelation();
        rel.kingdom = this;
        rel.otherKingdom = (BKingdom) kingdom;
        rel.relation = relation.getId();
        relations.add(rel);
    }

    @Override
    @Deprecated
    public Set<User> getMembers() {
        return new HashSet<>(members);
    }
}
