package com.igufguf.kingdomcraft.domain;

import io.ebean.Model;
import io.ebean.annotation.WhenCreated;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.time.Instant;

@Entity
public class Rank extends Model {

    @Id
    long id;

    @ManyToOne
    Kingdom kingdom;

    String name;

    String display;

    String prefix;

    String suffix;

    @WhenCreated
    Instant createdAt;

    public Rank(String name, Kingdom kingdom) {
        this.name = name;
        this.kingdom = kingdom;
    }

    public long getId() {
        return id;
    }

    public Kingdom getKingdom() {
        return kingdom;
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

    public Instant getCreatedAt() {
        return createdAt;
    }

}
