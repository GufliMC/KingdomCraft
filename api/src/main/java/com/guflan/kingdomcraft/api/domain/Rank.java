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

package com.guflan.kingdomcraft.api.domain;

import java.util.List;

public interface Rank extends Model {


    String getName();

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

    List<RankPermissionGroup> getPermissionGroups();

    RankPermissionGroup getPermissionGroup(String name);

    RankPermissionGroup createPermissionGroup(String name);

    int getMemberCount();
}
