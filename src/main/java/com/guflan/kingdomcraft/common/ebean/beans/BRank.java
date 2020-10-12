package com.guflan.kingdomcraft.common.ebean.beans;

import com.guflan.kingdomcraft.api.domain.models.Kingdom;
import com.guflan.kingdomcraft.api.domain.models.Rank;
import io.ebean.Model;
import io.ebean.annotation.ConstraintMode;
import io.ebean.annotation.DbForeignKey;
import io.ebean.annotation.WhenCreated;
import io.ebean.annotation.WhenModified;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "ranks")
@UniqueConstraint(columnNames = { "name", "kingdom_id" })
public class BRank extends Model implements Rank {

    @Id
    public long id;

    public String name;

    @ManyToOne
    @DbForeignKey(onDelete = ConstraintMode.CASCADE)
    public BKingdom kingdom;

    public String display;
    public String prefix;
    public String suffix;
    public int maxMembers;
    public int level;

    @WhenCreated
    public Date createdAt;

    @WhenModified
    public Date updatedAt;

    @Override
    public boolean delete() {
        kingdom.ranks.remove(this);
        if ( kingdom.defaultRank == this ) {
            kingdom.defaultRank = null;
        }
        return super.delete();
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

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public void setLevel(int level) {
        this.level = level;
    }
}
