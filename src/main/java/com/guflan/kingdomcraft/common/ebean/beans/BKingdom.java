package com.guflan.kingdomcraft.common.ebean.beans;

import com.guflan.kingdomcraft.api.domain.models.Kingdom;
import com.guflan.kingdomcraft.api.domain.models.Rank;
import com.guflan.kingdomcraft.api.domain.models.User;
import io.ebean.Model;
import io.ebean.annotation.ConstraintMode;
import io.ebean.annotation.*;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "kingdoms")
public class BKingdom extends Model implements Kingdom {

    public static List<BKingdom> INSTANCES = new ArrayList<>();

    public BKingdom() {
        INSTANCES.add(this);
    }

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
    @DbForeignKey(onDelete = ConstraintMode.SET_NULL)
    public BRank defaultRank;

    @OneToMany(mappedBy = "kingdom", fetch = FetchType.EAGER)
    public List<BRank> ranks;

//    @OneToMany(mappedBy = "kingdom", cascade = CascadeType.ALL)
//    public List<BRelation> relations;

//    @OneToMany(mappedBy = "kingdom")
//    public List<BUser> members;

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
    public Rank createRank(String name) {
        BRank rank = new BRank();
        rank.name = name;
        rank.kingdom = this;

        ranks = new ArrayList<>(ranks);
        ranks.add(rank);
        return rank;
    }

    @Override
    public void deleteRank(Rank rank) {
        ranks = new ArrayList<>(ranks);
        ranks.remove(rank);
    }
}
