package com.igufguf.kingdomcraft.domain;

import com.avaje.ebean.annotation.CreatedTimestamp;
import io.ebean.Model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Date;

@Table(
        uniqueConstraints=
        @UniqueConstraint(columnNames={"kingdom_id", "name"})
)
@Entity
public class Rank extends Model {

    @Id
    long id;

    @ManyToOne
    Kingdom kingdom;

    @Column(nullable = false)
    String name;

    String display;

    String prefix;

    String suffix;

    @CreatedTimestamp
    Date createdAt;

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

    public Date getCreatedAt() {
        return createdAt;
    }

}
