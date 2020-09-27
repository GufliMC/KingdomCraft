package com.guflan.kingdomcraft.common.ebean.beans;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "ranks")
public class Rank extends BaseModel {

    @Column(unique=true)
    final String name;

    @ManyToOne
    final Kingdom kingdom;

    String display;
    String prefix;
    String suffix;
    int maxMembers;

    public Rank(String name, Kingdom kingdom) {
        this.name = name;
        this.kingdom = kingdom;
    }

}
