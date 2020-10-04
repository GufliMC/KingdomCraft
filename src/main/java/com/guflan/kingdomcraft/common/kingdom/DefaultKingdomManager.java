package com.guflan.kingdomcraft.common.kingdom;

import com.guflan.kingdomcraft.api.KingdomCraftBridge;
import com.guflan.kingdomcraft.api.domain.User;
import com.guflan.kingdomcraft.api.managers.KingdomManager;
import com.guflan.kingdomcraft.api.domain.Kingdom;
import com.guflan.kingdomcraft.common.storage.Storage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class DefaultKingdomManager implements KingdomManager {

    private final KingdomCraftBridge bridge;
    private final Storage storage;

    private List<Kingdom> kingdoms = new ArrayList<>();

    public DefaultKingdomManager(KingdomCraftBridge bridge, Storage storage) {
        this.bridge = bridge;
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

            bridge.getEventManager().kingdomCreate(kingdom);

            return kingdom;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteKingdom(Kingdom kingdom) {

        // update locally
        for ( User u : bridge.getUserManager().getOnlineUsers() ) {
            if ( u.getKingdom() == kingdom ) {
                u.setKingdom(null);
            }
        }

        // update database

        // delete kingdom
        kingdoms.remove(kingdom);

        bridge.getEventManager().kingdomDelete(kingdom);
    }

    public void saveKingdom(Kingdom kingdom) {
        storage.saveKingdom(kingdom);
    }

    public List<User> getOnlineMembers(Kingdom kingdom) {
        return bridge.getUserManager().getOnlineUsers().stream().filter(u -> u.getKingdom() == kingdom).collect(Collectors.toList());
    }

}
