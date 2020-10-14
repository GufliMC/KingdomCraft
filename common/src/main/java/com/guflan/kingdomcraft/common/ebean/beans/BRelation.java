/*
 * This file is part of KingdomCraft.
 *
 * KingdomCraft is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * KingdomCraft is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with KingdomCraft. If not, see <https://www.gnu.org/licenses/>.
 */

package com.guflan.kingdomcraft.common.ebean.beans;

import com.guflan.kingdomcraft.api.domain.models.Kingdom;
import com.guflan.kingdomcraft.api.domain.models.Relation;
import com.guflan.kingdomcraft.api.domain.models.RelationType;
import io.ebean.Model;
import io.ebean.annotation.ConstraintMode;
import io.ebean.annotation.DbForeignKey;
import io.ebean.annotation.WhenCreated;
import io.ebean.annotation.WhenModified;

import javax.persistence.*;
import java.util.Date;

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
