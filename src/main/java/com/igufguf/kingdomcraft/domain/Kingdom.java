package com.igufguf.kingdomcraft.domain;

import io.ebean.Model;
import io.ebean.annotation.WhenCreated;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.time.Instant;
import java.util.List;

@Entity
public class Kingdom extends Model {

    @Id
    long id;

    String name;

    String display;

    String prefix;

    String suffix;

    String spawn;

    boolean inviteOnly = false;

    @OneToMany(mappedBy = "kingdom")
    List<Player> members;

    @OneToMany(mappedBy = "kingdom")
    List<Rank> ranks;

    @OneToOne
    Rank defaultRank;

    @WhenCreated
    Instant createdAt;

    public Kingdom(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getSpawn() {
        return spawn;
    }

    public void setSpawn(String spawn) {
        this.spawn = spawn;
    }

    public boolean isInviteOnly() {
        return inviteOnly;
    }

    public void setInviteOnly(boolean inviteOnly) {
        this.inviteOnly = inviteOnly;
    }

    public List<Player> getMembers() {
        return members;
    }

    public List<Rank> getRanks() {
        return ranks;
    }

    public Rank getDefaultRank() {
        return defaultRank;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

}

