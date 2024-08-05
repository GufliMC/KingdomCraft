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

package com.gufli.kingdomcraft.common.ebean.beans;

import com.gufli.kingdomcraft.api.domain.*;
import com.gufli.kingdomcraft.common.ebean.StorageContext;
import com.gufli.kingdomcraft.common.ebean.beans.query.QBUser;
import io.ebean.annotation.*;
import io.ebean.annotation.ConstraintMode;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "ranks")
@UniqueConstraint(columnNames = { "name", "kingdom_id" })
public class BRank extends BaseModel implements Rank {

    @Id
    public int id;

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

    @Formula(select = "(select count(id) from users where rank_id = t0.id)")
    public int memberCount;

    @WhenCreated
    public Instant createdAt;

    @WhenModified
    public Instant updatedAt;

    //

    public BRank() {
        super();
    }

    @Override
    public boolean delete() {
        kingdom.ranks.remove(this);
        if ( kingdom.defaultRank == this ) {
            kingdom.defaultRank = null;
        }
        Rank defaultRank = kingdom.defaultRank;
        StorageContext.players.values().stream().filter(user -> user.getRank() == this)
                .forEach(user -> user.setRank(defaultRank));
        return super.delete();
    }

    @Override
    public void save() {
        super.save();
    }

    // interface

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void renameTo(String name) {
        if ( this.kingdom.getRank(name) != null ) {
            throw new IllegalArgumentException("A rank with that name already exists.");
        }
        this.name = name;
    }

    @Override
    public Kingdom getKingdom() {
        return kingdom;
    }

    @Override
    public String getDisplay() {
        return display != null && !display.equals("") ? display : name;
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
    public List<RankAttribute> getAttributes() {
        return new ArrayList<>(attributes);
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
        return memberCount;
    }

    @Override
    public Map<UUID, String> getMembers() {
        QBUser alias = QBUser.alias();
        return new QBUser().rank.id.eq(this.id).select(alias.id, alias.name).findStream().collect(Collectors.toMap(BUser::getUniqueId, BUser::getName));
    }

    @Override
    public Rank clone(Kingdom kingdom, boolean withAttributes) {
        Rank clone = kingdom.getRank(this.name);
        if ( clone == null ) {
            clone = kingdom.createRank(this.name);
        }

        clone.setPrefix(this.prefix);
        clone.setSuffix(this.suffix);
        clone.setDisplay(this.display);
        clone.setMaxMembers(this.maxMembers);
        clone.setLevel(this.level);

        for (RankPermissionGroup rpg : this.permissionGroups ) {
            if ( clone.getPermissionGroup(rpg.getName()) != null ) {
                continue;
            }
            clone.createPermissionGroup(rpg.getName());
        }

        if ( withAttributes ) {
            for (RankAttribute attr : this.attributes ) {
                if ( clone.getAttribute(attr.getName()) != null ) {
                    continue;
                }
                clone.createAttribute(attr.getName()).setValue(attr.getValue());
            }
        }
        return clone;
    }
}
