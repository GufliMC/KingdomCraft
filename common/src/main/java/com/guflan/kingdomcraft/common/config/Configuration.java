package com.guflan.kingdomcraft.common.config;

import java.util.List;
import java.util.Set;

public interface Configuration {

    Object get(String path);

    boolean contains(String path);

    String getString(String path);

    boolean getBoolean(String path);

    int getInt(String path);

    double getDouble(String path);

    List<String> getStringList(String path);

    Configuration getConfigurationSection(String path);

    Set<String> getKeys(boolean deep);

}
