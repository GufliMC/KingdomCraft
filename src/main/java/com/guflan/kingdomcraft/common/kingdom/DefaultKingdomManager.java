package com.guflan.kingdomcraft.common.kingdom;

import com.guflan.kingdomcraft.api.KingdomCraftPlugin;
import com.guflan.kingdomcraft.api.domain.Rank;
import com.guflan.kingdomcraft.api.entity.EntityPlayer;
import com.guflan.kingdomcraft.api.managers.KingdomManager;
import com.guflan.kingdomcraft.api.domain.Kingdom;
import com.guflan.kingdomcraft.api.domain.Player;
import com.guflan.kingdomcraft.common.storage.Storage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
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
        try {
            Kingdom kingdom = storage.createKingdom(name).get();
            kingdoms.add(kingdom);

            /*
            Rank rank = storage.createRank("member", kingdom);;
            kingdom.getRanks().add(rank);
            kingdom.setDefaultRank(rank);
            */

            plugin.getEventManager().kingdomCreate(kingdom);

            return kingdom;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteKingdom(Kingdom kingdom) {

        // update locally
        for ( EntityPlayer p : plugin.getPlayerManager().getOnlinePlayers() ) {
            if ( p.getPlayer().getKingdom() == kingdom ) {
                p.getPlayer().setKingdom(null);
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

    public List<EntityPlayer> getOnlineMembers(Kingdom kingdom) {
        return plugin.getPlayerManager().getOnlinePlayers().stream().filter(p -> p.getPlayer().getKingdom() == kingdom).collect(Collectors.toList());
    }

}
