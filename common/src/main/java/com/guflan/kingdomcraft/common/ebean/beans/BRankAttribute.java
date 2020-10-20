package com.guflan.kingdomcraft.common.ebean.beans;

import com.guflan.kingdomcraft.api.domain.Rank;
import com.guflan.kingdomcraft.api.domain.RankAttribute;
import io.ebean.Model;
import io.ebean.annotation.ConstraintMode;
import io.ebean.annotation.DbForeignKey;
import io.ebean.annotation.Index;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Index(unique = true, columnNames = {"rank_id", "name"})
@Table(name = "rank_attributes")
public class BRankAttribute extends Model implements RankAttribute {

    @Id
    public long id;

    @ManyToOne
    @DbForeignKey(onDelete = ConstraintMode.CASCADE)
    public BRank rank;

    public String name;
    public String value;

    //

    @Override
    public boolean delete() {
        rank.attributes.remove(this);
        return super.delete();
    }

    @Override
    public void save() {
        super.save();
    }

    // interface

    @Override
    public Rank getRank() {
        return rank;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }
}
