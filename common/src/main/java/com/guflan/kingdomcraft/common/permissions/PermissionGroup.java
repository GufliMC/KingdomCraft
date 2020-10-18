package com.guflan.kingdomcraft.common.permissions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PermissionGroup {

    private final String name;
    private final List<String> worlds;
    private final List<String> permissions;
    private final List<String> inheritances;

    public PermissionGroup(String name, List<String> permissions, List<String> inheritances, List<String> worlds) {
        this.name = name;
        this.worlds = worlds;
        this.permissions = permissions;
        this.inheritances = inheritances;
    }

    public PermissionGroup(String name, List<String> permissions, List<String> inheritances) {
        this(name, permissions, inheritances, new ArrayList<>());
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

    public Map<String, Boolean> getPermissionsAsMap() {
        Map<String, Boolean> map = new HashMap<>();
        permissions.forEach(s -> map.put(s, !s.startsWith("-")));
        return map;
    }

    public List<String> getInheritances() {
        return inheritances;
    }
}
