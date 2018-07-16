package com.igufguf.kingdomcraft.managers;

import com.igufguf.kingdomcraft.KingdomCraftApi;
import com.igufguf.kingdomcraft.objects.KingdomUser;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Created by Joris on 12/07/2018 in project KingdomCraft.
 */
public class TeleportManager {

    private final KingdomCraftApi api;

    public TeleportManager(KingdomCraftApi api) {
        this.api = api;
    }

    public void startTeleporter(KingdomUser user, Location target, String msg, long delay) {
        user.setLocalData("teleporting", true);

        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(api.getPlugin(), () -> {
            Player p = user.getPlayer();
            if ( p == null || !p.isOnline() || !user.hasLocalData("teleporting") || !user.getLocalBoolean("teleporting") ) {
                return;
            }

            p.teleport(target);
            p.sendMessage(msg);
            user.setLocalData("teleporting", null);
        }, delay * 20);
    }

    public boolean isTeleporting(KingdomUser user) {
        return user.hasLocalData("teleporting") && user.getLocalBoolean("teleporting");
    }

    public void cancelTaleporter(KingdomUser user) {
        user.setLocalData("teleporting", null);

        if ( user.getPlayer() == null ) return;
        api.getPlugin().getMsg().send(user.getPlayer(), "teleportCancel");
    }
}
