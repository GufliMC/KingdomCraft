package com.gufli.kingdomcraft.bukkit.listeners;

import com.gufli.kingdomcraft.api.events.PlayerLoadedEvent;
import com.gufli.kingdomcraft.bukkit.KingdomCraftBukkitPlugin;

public class AdminJoinListener {

    private final KingdomCraftBukkitPlugin plugin;

    public AdminJoinListener(KingdomCraftBukkitPlugin plugin) {
        this.plugin = plugin;
        plugin.getKdc().getEventManager().addListener(PlayerLoadedEvent.class, this::onJoin);
    }

    public void onJoin(PlayerLoadedEvent e) {
        if ( !e.getPlayer().isAdmin() ) {
            return;
        }

        if ( !e.getPlayer().hasPermission("kingdom.admin") ) {
            e.getPlayer().setAdmin(false);
            plugin.getKdc().saveAsync(e.getPlayer().getUser());
            return;
        }

        plugin.getKdc().getMessages().send(e.getPlayer(), "adminModeJoin");
    }

}
