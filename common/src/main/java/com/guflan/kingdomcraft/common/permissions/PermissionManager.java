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

import com.guflan.kingdomcraft.api.domain.Rank;
import com.guflan.kingdomcraft.api.domain.RankPermissionGroup;
import com.guflan.kingdomcraft.api.domain.User;
import com.guflan.kingdomcraft.common.KingdomCraftImpl;
import com.guflan.kingdomcraft.common.config.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PermissionManager {

    private final List<PermissionGroup> groups = new ArrayList<>();

    private PermissionGroup defaultGroup;

    public PermissionManager(KingdomCraftImpl kdc, Configuration config) {
        reload(config);
    }

    public void addPermissionGroup(PermissionGroup group) {
        if ( groups.contains(group) ) {
            return;
        }
        groups.add(group);
    }

    public void removePermissionGroup(PermissionGroup group) {
        groups.remove(group);
    }

    public PermissionGroup getGroup(String name) {
        return groups.stream().filter(g -> g.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public List<PermissionGroup> getGroups() {
        return new ArrayList<>(groups);
    }

    public PermissionGroup getDefaultGroup() {
        return defaultGroup;
    }

    public Map<String, Boolean> getTotalPermissions(PermissionGroup group) {
        Map<String, Boolean> perms = group.getPermissionsAsMap();
        fillPermissionsMap(group, new ArrayList<>(), perms);
        return perms;
    }

    public Map<String, Boolean> getTotalPermissions(User user, String world) {
        Map<String, Boolean> permissions = new HashMap<>();

        List<PermissionGroup> groups = new ArrayList<>();
        if ( user.getRank() == null  ) {
            if ( defaultGroup == null ) {
                return permissions;
            }

            groups.add(defaultGroup);
        } else {
            Rank rank = user.getRank();
            for ( PermissionGroup group : this.getGroups() ) {
                RankPermissionGroup rpg = rank.getPermissionGroup(group.getName());
                if ( rpg == null && group.getRanks().isEmpty() && group.getRanks().stream()
                        .noneMatch(r -> r.equalsIgnoreCase(rank.getName())) ) {
                    continue;
                }
                if ( !group.getWorlds().isEmpty() && group.getWorlds().stream()
                        .noneMatch(w -> w.equalsIgnoreCase(world)) ) {
                    continue;
                }
                groups.add(group);
            }
        }

        groups.forEach(group -> permissions.putAll(getTotalPermissions(group)));
        return permissions;
    }

    //

    private void fillPermissionsMap(PermissionGroup group, List<String> passed, Map<String, Boolean> perms) {
        perms.putAll(group.getPermissionsAsMap());
        passed.add(group.getName());

        for ( String inheritance : group.getInheritances() ) {
            if ( passed.contains(inheritance) ) {
                continue;
            }

            PermissionGroup g = getGroup(inheritance);
            if ( g == null ) {
                continue;
            }

            fillPermissionsMap(g, passed, perms);
        }
    }

    public void reload(Configuration config) {
        groups.clear();
        for ( String key : config.getKeys(false) ) {
            Configuration cs = config.getConfigurationSection(key);

            PermissionGroup group = new PermissionGroup(key,
                    cs.contains("permissions") ? cs.getStringList("permissions") : new ArrayList<>(),
                    cs.contains("inheritances") ? cs.getStringList("inheritances") : new ArrayList<>(),
                    cs.contains("ranks") ? cs.getStringList("ranks") : new ArrayList<>(),
                    cs.contains("worlds") ? cs.getStringList("worlds") : new ArrayList<>());

            if ( key.equalsIgnoreCase("default") ) {
                defaultGroup = group;
                continue;
            }

            addPermissionGroup(group);
        }
    }

}
