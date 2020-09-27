package com.guflan.kingdomcraft.common.ebean.beans;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "kingdoms")
public class Kingdom extends BaseModel {

    @Column(unique=true)
    final String name;

    String display;
    String prefix;
    String suffix;
    boolean inviteOnly;
    int maxMembers;

    String spawn;

    @OneToOne
    Rank defaultRank;

    @OneToMany(mappedBy = "kingdom")
    List<Rank> ranks;

    @OneToMany(mappedBy = "kingdom")
    Set<Relation> relations;

    /*
    @OneToMany(mappedBy = "kingdom")
    private List<Player> members;
    */

    public Kingdom(String name) {
        this.name = name;
    }

}
