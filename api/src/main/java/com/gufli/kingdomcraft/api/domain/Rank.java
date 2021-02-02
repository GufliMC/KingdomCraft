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

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface Rank extends Model, AttributeHolder {

    int getId();

    String getName();

    void renameTo(String name);

    Kingdom getKingdom();

    String getDisplay();

    void setDisplay(String display);

    String getPrefix();

    void setPrefix(String prefix);

    String getSuffix();

    void setSuffix(String suffix);

    int getMaxMembers();

    void setMaxMembers(int maxMembers);

    int getLevel();

    void setLevel(int level);

    RankAttribute getAttribute(String name);

    RankAttribute createAttribute(String name);

    List<RankAttribute> getAttributes();

    List<RankPermissionGroup> getPermissionGroups();

    RankPermissionGroup getPermissionGroup(String name);

    RankPermissionGroup createPermissionGroup(String name);

    int getMemberCount();

    Map<UUID, String> getMembers();

    Rank clone(Kingdom kingdom, boolean withAttributes);
}
