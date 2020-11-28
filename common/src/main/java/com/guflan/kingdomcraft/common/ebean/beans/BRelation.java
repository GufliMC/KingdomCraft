/*
 * This file is part of KingdomCraft.
 *
 * KingdomCraft is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * KingdomCraft is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with KingdomCraft. If not, see <https://www.gnu.org/licenses/>.
 */

package com.guflan.kingdomcraft.common.ebean.beans;

import com.guflan.kingdomcraft.api.domain.Kingdom;
import com.guflan.kingdomcraft.api.domain.Relation;
import com.guflan.kingdomcraft.api.domain.RelationType;
import com.guflan.kingdomcraft.common.ebean.StorageContext;
import io.ebean.Model;
import io.ebean.annotation.*;
import io.ebean.annotation.ConstraintMode;

import javax.persistence.*;
import java.util.Date;

@DbName("kingdomcraft")
@Entity
@Table(name = "kingdom_relations")
public class BRelation extends Model implements Relation {

    @Id
    public long id;

    @ManyToOne
    @DbForeignKey(onDelete = ConstraintMode.CASCADE)
    public BKingdom kingdom;

    @ManyToOne
    @DbForeignKey(onDelete = ConstraintMode.CASCADE)
    public BKingdom otherKingdom;

    public int relation;

    public boolean isRequest;

    @WhenCreated
    public Date createdAt;

    @WhenModified
    public Date updatedAt;

    //

    public BRelation() {
        super("kingdomcraft");
    }

    @Override
    public boolean delete() {
        StorageContext.relations.remove(this);
        return super.delete();
    }

    @Override
    public void save() {

        super.save();
    }

    // interface

    @Override
    public Kingdom getKingdom1() {
        return kingdom;
    }

    @Override
    public Kingdom getKingdom2() {
        return otherKingdom;
    }

    @Override
    public RelationType getType() {
        return RelationType.fromId(relation);
    }

    @Override
    public boolean isRequest() {
        return isRequest;
    }
}
