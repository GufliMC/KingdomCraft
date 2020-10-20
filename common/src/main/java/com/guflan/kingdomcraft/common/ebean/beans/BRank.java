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

import com.guflan.kingdomcraft.api.domain.Kingdom;
import com.guflan.kingdomcraft.api.domain.Rank;
import com.guflan.kingdomcraft.api.domain.RankAttribute;
import com.guflan.kingdomcraft.api.domain.RankPermissionGroup;
import com.guflan.kingdomcraft.common.ebean.beans.query.QBRank;
import com.guflan.kingdomcraft.common.ebean.beans.query.QBUser;
import io.ebean.Model;
import io.ebean.annotation.ConstraintMode;
import io.ebean.annotation.DbForeignKey;
import io.ebean.annotation.WhenCreated;
import io.ebean.annotation.WhenModified;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    @OneToMany(mappedBy = "rank", fetch = FetchType.EAGER)
    public List<BRankAttribute> attributes;

    @OneToMany(mappedBy = "rank", fetch = FetchType.EAGER)
    public List<BRankPermissionGroup> permissionGroups;

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

    @Override
    public void save() {
        super.save();
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

    @Override
    public RankAttribute getAttribute(String name) {
        return attributes.stream().filter(p -> p.getName().equals(name)).findFirst().orElse(null);
    }

    @Override
    public RankAttribute createAttribute(String name) {
        return attributes.stream().filter(p -> p.getName().equals(name)).findFirst().orElseGet(() -> {
            BRankAttribute attribute = new BRankAttribute();
            attribute.rank = this;
            attribute.name = name;

            attributes.add(attribute);
            return attribute;
        });
    }

    @Override
    public List<RankPermissionGroup> getPermissionGroups() {
        return new ArrayList<>(permissionGroups);
    }

    @Override
    public RankPermissionGroup getPermissionGroup(String name) {
        return permissionGroups.stream().filter(g -> g.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    @Override
    public RankPermissionGroup createPermissionGroup(String name) {
        return permissionGroups.stream().filter(p -> p.getName().equals(name)).findFirst().orElseGet(() -> {
            BRankPermissionGroup permissionGroup = new BRankPermissionGroup();
            permissionGroup.rank = this;
            permissionGroup.name = name;

            permissionGroups.add(permissionGroup);
            return permissionGroup;
        });
    }

    @Override
    public int getMemberCount() {
        return new QBUser().rank.eq(this).findCount();
    }
}
