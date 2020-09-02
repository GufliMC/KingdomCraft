package com.igufguf.kingdomcraft.bukkit.integration;

import com.igufguf.kingdomcraft.api.domain.Player;
import com.igufguf.kingdomcraft.api.event.EventListener;
import org.bukkit.Bukkit;

public class IntegrationListener implements EventListener {

    private final BukkitIntegration integration;

    public IntegrationListener(BukkitIntegration integration) {
        this.integration = integration;
    }

    @Override
    public void onJoin(Player player) {
        integration.players.put(player, new BukkitOnlinePlayer(Bukkit.getPlayer(player.getUniqueId()), player));
    }

    @Override
    public void onLeave(Player player) {
        integration.players.remove(player);
    }
}
