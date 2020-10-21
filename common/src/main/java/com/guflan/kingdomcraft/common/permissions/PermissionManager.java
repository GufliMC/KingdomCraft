package com.guflan.kingdomcraft.common.permissions;

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
        load(config);
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

    public PermissionGroup getDefaultGroup() {
        return defaultGroup;
    }

    public Map<String, Boolean> getTotalPermissions(PermissionGroup group) {
        Map<String, Boolean> perms = group.getPermissionsAsMap();
        fillPermissionsMap(group, new ArrayList<>(), perms);
        return perms;
    }

    public Map<String, Boolean> getTotalPermissions(User user) {
        Map<String, Boolean> permissions = new HashMap<>();

        List<PermissionGroup> groups = new ArrayList<>();
        if ( user.getRank() == null  ) {
            if ( defaultGroup == null ) {
                return permissions;
            }

            groups.add(defaultGroup);
        } else {
            for (RankPermissionGroup rpg : user.getRank().getPermissionGroups()) {
                PermissionGroup group = getGroup(rpg.getName());
                if ( group != null ) {
                    groups.add(group);
                }
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

    private void load(Configuration config) {
        for ( String key : config.getKeys(false) ) {
            Configuration cs = config.getConfigurationSection(key);

            PermissionGroup group = new PermissionGroup(key,
                    cs.contains("permissions") ? cs.getStringList("permissions") : new ArrayList<>(),
                    cs.contains("inheritances") ? cs.getStringList("inheritances") : new ArrayList<>(),
                    cs.contains("worlds") ? cs.getStringList("worlds") : new ArrayList<>());

            if ( key.equalsIgnoreCase("default") ) {
                defaultGroup = group;
                continue;
            }

            addPermissionGroup(group);
        }
    }

}
