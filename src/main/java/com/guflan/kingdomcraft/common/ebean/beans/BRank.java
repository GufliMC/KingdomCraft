package com.guflan.kingdomcraft.common.ebean.beans;

import com.guflan.kingdomcraft.api.domain.Kingdom;
import com.guflan.kingdomcraft.api.domain.Rank;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "ranks")
public class BRank extends BaseModel implements Rank {

    @Column(unique=true)
    final String name;

    @ManyToOne
    final BKingdom kingdom;

    String display;
    String prefix;
    String suffix;
    int maxMembers;

    public BRank(String name, BKingdom kingdom) {
        this.name = name;
        this.kingdom = kingdom;
    }

    // interface

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
    public int getMaxMembers() {
        return maxMembers;
    }

    @Override
    public void setMaxMembers(int maxMembers) {
        this.maxMembers = maxMembers;
    }
}
