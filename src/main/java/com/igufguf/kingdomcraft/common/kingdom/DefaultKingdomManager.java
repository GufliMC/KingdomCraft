package com.igufguf.kingdomcraft.common.kingdom;

import com.igufguf.kingdomcraft.api.KingdomCraftPlugin;
import com.igufguf.kingdomcraft.api.domain.Rank;
import com.igufguf.kingdomcraft.api.managers.KingdomManager;
import com.igufguf.kingdomcraft.api.domain.Kingdom;
import com.igufguf.kingdomcraft.api.domain.Player;
import com.igufguf.kingdomcraft.common.storage.Storage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DefaultKingdomManager implements KingdomManager {

    private final KingdomCraftPlugin plugin;
    private final Storage storage;

    private List<Kingdom> kingdoms = new ArrayList<>();

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
        Kingdom kingdom = plugin.getFactory().createKingdom(name);
        kingdoms.add(kingdom);

        Rank rank = plugin.getFactory().createRank("Member", kingdom);
        kingdom.getRanks().add(rank);
        kingdom.setDefaultRank(rank);

        plugin.getEventManager().kingdomCreate(kingdom);

        return kingdom;
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
        kingdoms.remove(kingdom);

        plugin.getEventManager().kingdomDelete(kingdom);
    }

    public void saveKingdom(Kingdom kingdom) {
        storage.saveKingdom(kingdom);
    }

    public List<Player> getOnlineMembers(Kingdom kingdom) {
        return plugin.getPlayerManager().getOnlinePlayers().stream().filter(p -> p.getKingdom() == kingdom).collect(Collectors.toList());
    }

}
