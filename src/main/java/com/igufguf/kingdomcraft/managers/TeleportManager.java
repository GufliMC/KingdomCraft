package com.igufguf.kingdomcraft.managers;

import com.igufguf.kingdomcraft.KingdomCraftApi;
import com.igufguf.kingdomcraft.objects.KingdomUser;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

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
