package com.igufguf.kingdomcraft.handlers;

import com.igufguf.kingdomcraft.KingdomCraft;
import com.igufguf.kingdomcraft.api.handlers.KingdomFlagHandler;
import com.igufguf.kingdomcraft.api.models.flags.KingdomFlag;
import com.igufguf.kingdomcraft.api.models.kingdom.Kingdom;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

import static com.igufguf.kingdomcraft.api.models.flags.KingdomFlag.FRIENDLYFIRE;
import static com.igufguf.kingdomcraft.api.models.flags.KingdomFlag.INVITE_ONLY;

/**
 * Copyrighted 2018 iGufGuf
 *
 * This file is part of KingdomCraft.
 *
 * Kingdomcraft is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * KingdomCraft is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with KingdomCraft.  If not, see <http://www.gnu.org/licenses/>.
 *
 **/
public class SimpleFlagHandler implements KingdomFlagHandler  {

    private final KingdomCraft plugin;

    private final List<KingdomFlag> flagList = new ArrayList<>();

    public SimpleFlagHandler(KingdomCraft plugin) {
        this.plugin = plugin;

        register(INVITE_ONLY);
        register(FRIENDLYFIRE);
    }

    // registry

    @Override
    public void register(KingdomFlag flag) {
        if ( flagList.contains(flag) ) return;
        flagList.add(flag);
    }

    @Override
    public void registerAll(Collection<KingdomFlag> flags) {
        for ( KingdomFlag flag : flags) {
            register(flag);
        }
    }

    @Override
    public void registerAll(KingdomFlag... flags) {
        for ( KingdomFlag flag : flags) {
            register(flag);
        }
    }

    @Override
    public KingdomFlag getFlag(String name) {
        for ( KingdomFlag flag : flagList ) {
            if ( flag.getName().equalsIgnoreCase(name) ) return flag;
        }
        return null;
    }

    @Override
    public List<KingdomFlag> getAllFlags() {
        return flagList;
    }


    // kingdom api

    private Map<KingdomFlag, Object> defaultFlags = null;

    private void loadDefaultFlags() {
        defaultFlags = new HashMap<>();

        if ( !(plugin.getApi().getKingdomHandler() instanceof SimpleKingdomHandler) ) {
            return;
        }

        SimpleKingdomHandler kh = (SimpleKingdomHandler) plugin.getApi().getKingdomHandler();
        if ( !kh.getConfiguration().contains("flags") ) {
            return;
        }

        ConfigurationSection cs = kh.getConfiguration().getConfigurationSection("flags");

        for ( String flagname : cs.getKeys(false) ) {
            KingdomFlag flag = getFlag(flagname);
            if ( flag == null ) continue;

            Object value = cs.get(flagname);
            if ( !flag.getType().isAssignableFrom(value.getClass()) ) {
                plugin.getLogger().info("The default flag '" + flag.getName() + "' has an invalid value, it should be of type '" + flag.getClass().getSimpleName() + "'");
                continue;
            }

            defaultFlags.put(flag, value);
        }
    }

    private Map<KingdomFlag, Object> getDefaultFlags() {
        if ( defaultFlags == null ) loadDefaultFlags();
        return defaultFlags;
    }

    @Override
    public Map<KingdomFlag, Object> getFlags(Kingdom kd) {
        Map<KingdomFlag, Object> result = new HashMap<>();

        Map<String, Object> flags = kd.getFlags();
        for ( String flagname : flags.keySet() ) {
            KingdomFlag flag = getFlag(flagname);
            if ( flag == null ) continue;
            result.put(flag, flags.get(flagname));
        }

        for ( KingdomFlag defaultFlag : getDefaultFlags().keySet() ) {
            if ( result.containsKey(defaultFlag) ) continue;
            result.put(defaultFlag, defaultFlags.get(defaultFlag));
        }

        return result;
    }

    @Override
    public void setFlag(Kingdom kd, KingdomFlag flag, Object value) {
        Map<String, Object> flags = kd.getFlags();

        if ( flags == null ) {
            if ( value == null ) return;
            flags = new HashMap<>();
        }

        if ( value != null ) {
            flags.put(flag.getName(), value);
        } else {
            flags.remove(flag.getName());
        }
    }

    @Override
    public boolean hasFlag(Kingdom kd, KingdomFlag flag) {
        Map<KingdomFlag, Object> flags = getFlags(kd);
        return flags.containsKey(flag);
    }

    @Override
    public <T> T getFlag(Kingdom kd, KingdomFlag<T> flag) {
        Object result = getFlags(kd).get(flag);

        if ( result == null ) {
            return flag.getDefaultValue();
        }

        return flag.parse(result);
    }


}
