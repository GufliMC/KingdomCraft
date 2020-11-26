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

package com.guflan.kingdomcraft.common.permissions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PermissionGroup {

    private final String name;
    private final List<String> permissions;
    private final List<String> inheritances;
    private final List<String> ranks;
    private final List<String> worlds;

    public PermissionGroup(String name, List<String> permissions, List<String> inheritances, List<String> ranks, List<String> worlds) {
        this.name = name;
        this.worlds = worlds;
        this.permissions = permissions;
        this.inheritances = inheritances;
        this.ranks = ranks;
    }

    public PermissionGroup(String name, List<String> permissions, List<String> inheritances, List<String> ranks) {
        this(name, permissions, inheritances, ranks, new ArrayList<>());
    }

    public PermissionGroup(String name, List<String> permissions, List<String> inheritances) {
        this(name, permissions, inheritances, new ArrayList<>(), new ArrayList<>());
    }

    public String getName() {
        return name;
    }

    public List<String> getWorlds() {
        return worlds;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public List<String> getRanks() {
        return ranks;
    }

    public Map<String, Boolean> getPermissionsAsMap() {
        Map<String, Boolean> map = new HashMap<>();
        permissions.forEach(s -> map.put(s, !s.startsWith("-")));
        return map;
    }

    public List<String> getInheritances() {
        return inheritances;
    }
}
