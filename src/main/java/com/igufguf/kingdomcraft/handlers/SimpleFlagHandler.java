package com.igufguf.kingdomcraft.handlers;

import com.igufguf.kingdomcraft.KingdomCraft;
import com.igufguf.kingdomcraft.api.exceptions.PluginNotEnabledException;
import com.igufguf.kingdomcraft.api.handlers.KingdomFlagHandler;
import com.igufguf.kingdomcraft.api.models.flags.KingdomFlag;
import com.igufguf.kingdomcraft.api.models.kingdom.Kingdom;

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

    private final List<KingdomFlag> flagList = new ArrayList<>();

    public SimpleFlagHandler(KingdomCraft plugin) {
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

    @Override
    public List<KingdomFlag> getFlags(Kingdom kd) {
        Map<String, Object> flags = kd.getFlags();

        if ( flags == null || flags.isEmpty() ) {
            return new ArrayList<>();
        }

        List<KingdomFlag> flagList = new ArrayList<>();
        for ( String flagname : flags.keySet() ) {
            KingdomFlag flag = getFlag(flagname);
            if ( flag == null ) continue;
            flagList.add(flag);
        }

        return flagList;
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
        Map<String, Object> flags = kd.getFlags();
        if ( flags == null || flags.isEmpty() ) return false;
        return flags.containsKey(flag.getName());
    }

    @Override
    public <T> T getFlagValue(Kingdom kd, KingdomFlag<T> flag) {
        Map<String, Object> flags = kd.getFlags();
        if ( flags == null || flags.isEmpty() ) return null;

        return flag.parse(flags.get(flag.getName()));
    }


}
