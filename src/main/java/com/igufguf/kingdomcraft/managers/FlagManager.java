package com.igufguf.kingdomcraft.managers;

import com.igufguf.kingdomcraft.KingdomCraftApi;
import com.igufguf.kingdomcraft.objects.KingdomFlag;
import com.igufguf.kingdomcraft.objects.KingdomObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

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
public class FlagManager {

    // DEFAULT FLAGS
    public static final KingdomFlag INVITE_ONLY = new KingdomFlag("invite-only");
    public static final KingdomFlag FRIENDLYFIRE = new KingdomFlag("friendlyfire");

    private final List<KingdomFlag> flagList = new ArrayList<>();

    public FlagManager(KingdomCraftApi api) {
        register(INVITE_ONLY);
    }

    public void register(KingdomFlag flag) {
        if ( flagList.contains(flag) ) return;
        flagList.add(flag);
    }

    public void registerAll(Collection<KingdomFlag> flags) {
        for ( KingdomFlag flag : flags) {
            register(flag);
        }
    }

    public KingdomFlag getFlag(String name) {
        for ( KingdomFlag flag : flagList ) {
            if ( flag.getName().equalsIgnoreCase(name) ) return flag;
        }
        return null;
    }

    public List<KingdomFlag> getSettedFlags(KingdomObject kd) {
        List<String> flags = kd.getList("flags", String.class);

        if ( flags == null ) {
            return new ArrayList<>();
        }

        List<KingdomFlag> setFlags = new ArrayList<>();

        for ( String flagdata : new ArrayList<>(flags) ) {
            String[] split = flagdata.split(Pattern.quote(":"));

            KingdomFlag flag = getFlag(split[0]);
            if ( flag == null ) continue;

            setFlags.add(flag);
        }

        return setFlags;
    }

    public void setFlag(KingdomObject kd, KingdomFlag flag, int value) {
        setFlag(kd, flag, value + "");
    }

    public void setFlag(KingdomObject kd, KingdomFlag flag, boolean value) {
        setFlag(kd, flag, value + "");
    }

    public void setFlag(KingdomObject kd, KingdomFlag flag, String value) {
        List<String> flags = kd.getList("flags", String.class);

        if ( flags == null ) {
            if ( value == null ) return;
            flags = new ArrayList<>();
        } else {

            // remove old flag value
            for ( String flagdata : new ArrayList<>(flags) ) {
                String[] split = flagdata.split(Pattern.quote(":"));

                if ( split[0].equalsIgnoreCase(flag.getName()) ) {
                    flags.remove(flagdata);
                }
            }
        }

        if ( value != null ) {
            flags.add(flag.getName() + ":" + value);
        }

        kd.setData("flags", flags);
    }

    public String getFlagValue(KingdomObject kd, KingdomFlag flag) {
        List<String> flags = kd.getList("flags", String.class);
        if ( flags == null ) return null;

        for ( String flagdata : new ArrayList<>(flags) ) {
            String[] split = flagdata.split(Pattern.quote(":"));

            if ( !split[0].equalsIgnoreCase(flag.getName()) ) continue;

            if ( split.length == 1 ) return "true"; // old version, just return true
            return split[1];
        }

        return null;
    }

    public boolean getFlagBoolean(KingdomObject kd, KingdomFlag flag) {
        return Boolean.valueOf(getFlagValue(kd, flag));
    }

    public int getFlagInteger(KingdomObject kd, KingdomFlag flag) {
        try {
            return Integer.valueOf(getFlagValue(kd, flag));
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

}
