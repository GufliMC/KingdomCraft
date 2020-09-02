package com.igufguf.kingdomcraft.common.storage.implementation;


import com.igufguf.kingdomcraft.api.domain.Factory;
import com.igufguf.kingdomcraft.api.domain.Kingdom;
import com.igufguf.kingdomcraft.api.domain.Player;
import com.igufguf.kingdomcraft.api.domain.Rank;
import com.igufguf.kingdomcraft.common.storage.StorageImplementation;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class H2StorageImplementation implements StorageImplementation {

    // TODO

    public H2StorageImplementation() {

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
