package com.igufguf.kingdomcraft.managers;

import com.igufguf.kingdomcraft.KingdomCraftApi;
import com.igufguf.kingdomcraft.objects.KingdomRank;
import com.igufguf.kingdomcraft.objects.KingdomUser;
import org.bukkit.permissions.PermissionAttachment;

import java.util.regex.Pattern;

/**
 * Created by Joris on 11/07/2018 in project KingdomCraft.
 */
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
