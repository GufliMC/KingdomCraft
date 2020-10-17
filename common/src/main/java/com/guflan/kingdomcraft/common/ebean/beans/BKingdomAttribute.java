package com.guflan.kingdomcraft.common.ebean.beans;

import com.guflan.kingdomcraft.api.domain.Kingdom;
import com.guflan.kingdomcraft.api.domain.KingdomAttribute;
import io.ebean.Model;
import io.ebean.annotation.ConstraintMode;
import io.ebean.annotation.DbForeignKey;
import io.ebean.annotation.Index;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Index(unique = true, columnNames = {"kingdom_id", "name"})
@Table(name = "kingdom_attributes")
public class BKingdomAttribute extends Model implements KingdomAttribute {

    @Id
    public long id;

    @ManyToOne
    @DbForeignKey(onDelete = ConstraintMode.CASCADE)
    public BKingdom kingdom;

    public String name;
    public String value;

    // interface

    @Override
    public Kingdom getKingdom() {
        return kingdom;
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
