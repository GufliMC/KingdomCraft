package com.igufguf.kingdomcraft.listeners;

import com.igufguf.kingdomcraft.KingdomCraft;
import com.igufguf.kingdomcraft.objects.KingdomObject;
import com.igufguf.kingdomcraft.objects.KingdomUser;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerRespawnEvent;

public class RespawnListener extends EventListener {

    public RespawnListener(KingdomCraft plugin) {
        super(plugin);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        KingdomUser user = plugin.getApi().getUserManager().getUser(e.getPlayer());
        if ( user == null ) return;

        KingdomObject kingdom = plugin.getApi().getUserManager().getKingdom(user);
        if ( kingdom == null ) return;
        if ( kingdom.getSpawn() == null ) return;

        e.setRespawnLocation(kingdom.getSpawn());
        e.getPlayer().teleport(kingdom.getSpawn());
    }

}
