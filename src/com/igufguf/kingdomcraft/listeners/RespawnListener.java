package com.igufguf.kingdomcraft.listeners;

import com.igufguf.kingdomcraft.KingdomCraft;
import com.igufguf.kingdomcraft.objects.KingdomUser;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerRespawnEvent;

public class RespawnListener extends com.igufguf.kingdomcraft.listeners.EventListener {

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        KingdomUser user = KingdomCraft.getApi().getUser(e.getPlayer());
        if ( user != null && user.getKingdom() != null && user.getKingdom().getSpawn() != null ) {
            e.setRespawnLocation(user.getKingdom().getSpawn());
            e.getPlayer().teleport(user.getKingdom().getSpawn());
        }
    }

}
