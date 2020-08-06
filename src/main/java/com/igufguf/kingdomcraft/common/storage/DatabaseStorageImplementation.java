package com.igufguf.kingdomcraft.common.storage;


import com.igufguf.kingdomcraft.api.models.Kingdom;
import com.igufguf.kingdomcraft.api.models.Player;
import com.igufguf.kingdomcraft.api.models.Rank;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DatabaseStorageImplementation implements StorageImplementation {

    // TODO

    public DatabaseStorageImplementation() {

    }

    @Override
    public List<Player> getPlayers() {
        return null;
    }

    @Override
    public Player getPlayer(String name) {
        return null;
    }

    @Override
    public Player getPlayer(UUID uuid) {
        return null;
    }

    @Override
    public void savePlayer(Player player) {

    }

    @Override
    public List<Kingdom> getKingdoms() {
        return new ArrayList<>();
    }



    @Override
    public void saveKingdom(Kingdom kingdom) {

    }

    @Override
    public void saveRank(Rank rank) {

    }

    @Override
    public void deleteKingdom(Kingdom kingdom) {

    }
}
