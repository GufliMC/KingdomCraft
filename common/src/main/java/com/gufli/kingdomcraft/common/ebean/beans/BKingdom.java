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
import com.gufli.kingdomcraft.api.entity.PlatformLocation;
import com.gufli.kingdomcraft.common.ebean.StorageContext;
import com.gufli.kingdomcraft.common.ebean.beans.query.QBUser;
import io.ebean.annotation.*;
import io.ebean.annotation.ConstraintMode;

import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Table(name = "kingdoms")
public class BKingdom extends BaseModel implements Kingdom {

    @Id
    public int id;

    @Column(unique=true)
    public String name;

    public String display;
    public String prefix;
    public String suffix;

    @Convert(converter = PlatformLocationConverter.class, attributeName = "spawn")
    public PlatformLocation spawn;

    public boolean inviteOnly;
    public int maxMembers;

    @OneToOne
    @DbForeignKey(onDelete = ConstraintMode.SET_NULL)
    public BRank defaultRank;

    @OneToMany(mappedBy = "kingdom", fetch = FetchType.EAGER)
    public List<BRank> ranks;

    @OneToMany(mappedBy = "kingdom", fetch = FetchType.EAGER)
    public List<BKingdomAttribute> attributes;

    @Formula(select = "(select count(id) from users where kingdom_id = t0.id)")
    public int memberCount;

    @WhenCreated
    Instant createdAt;

    @WhenModified
    Instant updatedAt;

    //

    public BKingdom() {
        super();
    }

    @Override
    public boolean delete() {
        StorageContext.kingdoms.remove(this);
        StorageContext.players.values().stream().filter(user -> user.getKingdom() == this)
                .forEach(user -> user.setKingdom(null));
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
        if (StorageContext.kingdoms.stream().anyMatch(k -> k.getName().equalsIgnoreCase(name))) {
            throw new IllegalArgumentException("A kingdom with that name already exists.");
        }
        this.name = name;
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
    public boolean isInviteOnly() {
        return inviteOnly;
    }

    @Override
    public void setInviteOnly(boolean inviteOnly) {
        this.inviteOnly = inviteOnly;
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
    public Rank getDefaultRank() {
        return defaultRank;
    }

    @Override
    public void setDefaultRank(Rank defaultRank) {
        if ( !this.ranks.contains(defaultRank) ) {
            throw new IllegalArgumentException("The given defaultRank does not belong to this kingdom.");
        }
        this.defaultRank = (BRank) defaultRank;
    }

    @Override
    public List<Rank> getRanks() {
        return new ArrayList<>(ranks);
    }

    @Override
    public Rank getRank(String name) {
        return ranks.stream().filter(r -> r.name.equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    @Override
    public Rank createRank(String name) {
        if ( getRank(name) != null ) {
            throw new IllegalArgumentException("A rank with that name already exists.");
        }
        BRank rank = new BRank();
        rank.name = name;
        rank.kingdom = this;

        ranks.add(rank);
        return rank;
    }

    @Override
    public KingdomAttribute getAttribute(String name) {
        return attributes.stream().filter(p -> p.getName().equals(name)).findFirst().orElse(null);
    }

    @Override
    public KingdomAttribute createAttribute(String name) {
        return attributes.stream().filter(p -> p.getName().equals(name)).findFirst().orElseGet(() -> {
            BKingdomAttribute attribute = new BKingdomAttribute();
            attribute.kingdom = this;
            attribute.name = name;

            attributes.add(attribute);
            return attribute;
        });
    }

    @Override
    public PlatformLocation getSpawn() {
        return this.spawn;
    }

    @Override
    public void setSpawn(PlatformLocation location) {
        this.spawn = location;
    }

    @Override
    public int getMemberCount() {
        return memberCount;
    }

    @Override
    public Map<UUID, String> getMembers() {
        QBUser alias = QBUser.alias();
        return new QBUser().kingdom.id.eq(this.id).select(alias.id, alias.name).findStream().collect(Collectors.toMap(BUser::getUniqueId, BUser::getName));
    }

    @Override
    public Instant getCreatedAt() {
        return createdAt;
    }

    @Override
    public void copyTo(Kingdom target, boolean withAttributes) {
        if ( this.prefix != null ) {
            target.setPrefix(this.prefix.replace("{kingdom_name}", target.getName()));
        }
        if ( this.suffix != null ) {
            target.setSuffix(this.suffix.replace("{kingdom_name}", target.getName()));
        }
        if ( this.display != null ) {
            target.setDisplay(this.display.replace("{kingdom_name}", target.getName()));
        }
        target.setMaxMembers(this.maxMembers);
        target.setInviteOnly(this.inviteOnly);

        if ( withAttributes ) {
            for (KingdomAttribute attr : this.attributes ) {
                if ( target.getAttribute(attr.getName()) != null ) {
                    continue;
                }
                target.createAttribute(attr.getName()).setValue(attr.getValue());
            }
        }
    }
}
