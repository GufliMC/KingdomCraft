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

package com.gufli.kingdomcraft.api.domain;

import com.gufli.kingdomcraft.api.entity.PlatformLocation;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface Kingdom extends Model, AttributeHolder {

    int getId();

    String getName();

    void renameTo(String name);

    String getDisplay();

    void setDisplay(String display);

    String getPrefix();

    void setPrefix(String prefix);

    String getSuffix();

    void setSuffix(String suffix);

    boolean isInviteOnly();

    void setInviteOnly(boolean inviteOnly);

    int getMaxMembers();

    void setMaxMembers(int maxMembers);

    Rank getDefaultRank();

    void setDefaultRank(Rank defaultRank);

    List<Rank> getRanks();

    Rank getRank(String name);

    Rank createRank(String name);

    KingdomAttribute getAttribute(String name);

    KingdomAttribute createAttribute(String name);

    PlatformLocation getSpawn();

    void setSpawn(PlatformLocation location);

    int getMemberCount();

    Map<UUID, String> getMembers();

    Instant getCreatedAt();

    default boolean isTemplate() {
        return getId() == -1;
    }

   void copyTo(Kingdom kingdom, boolean withAttributes);

}
