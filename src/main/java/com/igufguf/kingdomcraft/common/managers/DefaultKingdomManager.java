package com.igufguf.kingdomcraft.common.managers;

import com.igufguf.kingdomcraft.api.KingdomCraftPlugin;
import com.igufguf.kingdomcraft.api.handlers.KingdomManager;
import com.igufguf.kingdomcraft.api.models.Kingdom;
import com.igufguf.kingdomcraft.api.models.Player;
import com.igufguf.kingdomcraft.common.storage.Storage;

import java.util.List;
import java.util.stream.Collectors;

public class DefaultKingdomManager implements KingdomManager {

    private final KingdomCraftPlugin plugin;
    private final Storage storage;

    private List<Kingdom> kingdoms;

    public DefaultKingdomManager(KingdomCraftPlugin plugin, Storage storage) {
        this.plugin = plugin;
        this.storage = storage;
    }

    public List<Kingdom> getKingdoms() {
        return kingdoms;
    }

    public Kingdom getKingdom(String name) {
        return kingdoms.stream().filter(k -> k.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    @Override
    public Kingdom createKingdom(String name) {
        return null;
    }

    public void deleteKingdom(Kingdom kingdom) {

        // update locally
        for ( Player p : plugin.getPlayerManager().getOnlinePlayers() ) {
            if ( p.getKingdom() == kingdom ) {
                p.setKingdom(null);
            }
        }

        // update database

        // delete kingdom

    }

    public void saveKingdom(Kingdom kingdom) {
        storage.saveKingdom(kingdom);
    }

    public List<Player> getOnlineMembers(Kingdom kingdom) {
        return plugin.getPlayerManager().getOnlinePlayers().stream().filter(p -> p.getKingdom() == kingdom).collect(Collectors.toList());
    }

}
