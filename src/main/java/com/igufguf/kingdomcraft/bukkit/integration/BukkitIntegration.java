package com.igufguf.kingdomcraft.bukkit.integration;

import com.igufguf.kingdomcraft.api.KingdomCraftPlugin;
import com.igufguf.kingdomcraft.api.domain.Player;
import com.igufguf.kingdomcraft.api.integration.Integration;
import com.igufguf.kingdomcraft.api.integration.OnlinePlayer;

import java.util.HashMap;
import java.util.Map;

public class BukkitIntegration implements Integration {

    private final KingdomCraftPlugin plugin;

    protected Map<Player, OnlinePlayer> players = new HashMap<>();

    public BukkitIntegration(KingdomCraftPlugin plugin) {
        this.plugin = plugin;
        plugin.getEventManager().addListener(new IntegrationListener(this));
    }

    @Override
    public OnlinePlayer getOnlinePlayer(Player player) {
        return players.get(player);
    }

}
