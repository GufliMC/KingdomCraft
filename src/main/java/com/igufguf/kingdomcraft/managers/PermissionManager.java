package com.igufguf.kingdomcraft.managers;

import com.igufguf.kingdomcraft.KingdomCraftApi;
import com.igufguf.kingdomcraft.objects.KingdomObject;
import com.igufguf.kingdomcraft.objects.KingdomRank;
import com.igufguf.kingdomcraft.objects.KingdomUser;
import org.bukkit.permissions.PermissionAttachment;

import java.util.Map;
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
public class PermissionManager {

    private final KingdomCraftApi api;

    public PermissionManager(KingdomCraftApi api) {
        this.api = api;
    }

    public void refresh(KingdomUser user) {
        clear(user);

        if ( !api.isWorldEnabled(user.getPlayer().getWorld()) ) return;

        KingdomRank rank = api.getUserManager().getRank(user);
        if ( rank == null ) return;

        PermissionAttachment pa = user.getPlayer().addAttachment(api.getPlugin());
        setup(pa, rank);

        user.setLocalData("permissions", pa);
    }

    public void clear(KingdomUser user) {
        PermissionAttachment pa = user.hasLocalData("permissions") ? (PermissionAttachment) user.getLocalData("permissions") : null;
        if ( pa == null ) return;

        pa.remove();
    }

    private void setup(PermissionAttachment pa, KingdomRank rank) {
        setup(pa, rank, false);
    }

    private void setup(PermissionAttachment pa, KingdomRank rank, boolean inverse) {
        KingdomObject ko = rank.getKingdom();

        for ( Map.Entry<String, Boolean> perm : rank.getPermissions().entrySet() ) {

            // inheritances, if the permission is a rank name, setup the permissions for that rank
            KingdomRank r = ko.getRank(perm.getKey());
            if ( r != null ) {
                setup(pa, r, !perm.getValue());
                continue;
            }

            pa.setPermission(perm.getKey(), inverse ? !perm.getValue() : perm.getValue());
        }
    }

}
