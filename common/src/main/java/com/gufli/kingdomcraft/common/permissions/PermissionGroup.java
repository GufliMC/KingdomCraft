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

package com.gufli.kingdomcraft.common.permissions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Collections;

public class PermissionGroup {

    private final String name;
    private final List<String> permissions;
    private final List<String> inheritances;
    private final List<String> ranks;
    private final List<String> worlds;
    private final List<String> externals;
    private final Map<String, Boolean> permissionsMap;

    public PermissionGroup(String name, List<String> permissions, List<String> inheritances, List<String> ranks, List<String> worlds, List<String> externals) {
        this.name = name;
        this.worlds = Collections.unmodifiableList(worlds);
        this.permissions = Collections.unmodifiableList(permissions);
        this.inheritances = Collections.unmodifiableList(inheritances);
        this.ranks = Collections.unmodifiableList(ranks);
        this.externals = Collections.unmodifiableList(externals);
        var permissionsMap = new HashMap<String, Boolean>();
        permissions.forEach(s -> permissionsMap.put(s, !s.startsWith("-")));
        this.permissionsMap = Collections.unmodifiableMap(permissionsMap);
    }

    public PermissionGroup(String name, List<String> permissions, List<String> inheritances, List<String> ranks, List<String> worlds) {
        this(name, permissions, inheritances, ranks, worlds, new ArrayList<>());
    }

    public PermissionGroup(String name, List<String> permissions, List<String> inheritances, List<String> ranks) {
        this(name, permissions, inheritances, ranks, new ArrayList<>(), new ArrayList<>());
    }

    public PermissionGroup(String name, List<String> permissions, List<String> inheritances) {
        this(name, permissions, inheritances, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
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
        return permissionsMap;
    }

    public List<String> getInheritances() {
        return inheritances;
    }

    /**
     * This is dependend on the implementation. For bukkit, this will be used to determine the groups for vault.
     *
     * @return list of externals
     */
    public List<String> getExternals() {
        return externals;
    }
}
