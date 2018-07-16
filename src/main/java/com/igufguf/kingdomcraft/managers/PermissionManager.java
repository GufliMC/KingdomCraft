package com.igufguf.kingdomcraft.managers;

import com.igufguf.kingdomcraft.KingdomCraftApi;
import com.igufguf.kingdomcraft.objects.KingdomRank;
import com.igufguf.kingdomcraft.objects.KingdomUser;
import org.bukkit.permissions.PermissionAttachment;

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

    public void refreshPermissions(KingdomUser user) {
        PermissionAttachment pa = user.hasLocalData("permissions") ? (PermissionAttachment) user.getLocalData("permissions") : null;
        if ( pa != null ) pa.remove();

        KingdomRank rank = api.getUserManager().getRank(user);
        if ( rank == null || !api.isWorldEnabled(user.getPlayer().getWorld())) return;

        pa = user.getPlayer().addAttachment(api.getPlugin());

        for ( String perm : rank.getPermissions() ) {
            if ( perm.startsWith("-") ) {
                pa.setPermission(perm.replaceFirst(Pattern.quote("-"), "").trim(), false);
            } else {
                pa.setPermission(perm.trim(), true);
            }
        }

        user.setLocalData("permissions", pa);
    }

}
