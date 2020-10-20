package com.guflan.kingdomcraft.common.permissions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PermissionManager {

    private final List<PermissionGroup> groups = new ArrayList<>();

    public PermissionManager() {

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

    public Map<String, Boolean> getTotalPermissions(PermissionGroup group) {
        Map<String, Boolean> perms = group.getPermissionsAsMap();
        fillPermissionsMap(group, new ArrayList<>(), perms);
        return perms;
    }

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

}
