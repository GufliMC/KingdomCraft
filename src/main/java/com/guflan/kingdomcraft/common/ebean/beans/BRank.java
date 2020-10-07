package com.guflan.kingdomcraft.common.ebean.beans;

import com.guflan.kingdomcraft.api.domain.Kingdom;
import com.guflan.kingdomcraft.api.domain.Rank;
import io.ebean.annotation.WhenCreated;
import io.ebean.annotation.WhenModified;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "ranks")
public class BRank implements Rank {

    @Id
    public long id;

    public String name;

    @ManyToOne
    public BKingdom kingdom;

    public String display;
    public String prefix;
    public String suffix;
    public int maxMembers;

    @WhenCreated
    public Instant createdAt;

    @WhenModified
    public Instant updatedAt;

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
