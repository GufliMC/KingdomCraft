package com.guflan.kingdomcraft.common.storage;


import com.guflan.kingdomcraft.api.domain.Kingdom;
import com.guflan.kingdomcraft.api.domain.User;
import com.guflan.kingdomcraft.api.domain.Rank;

import java.util.List;
import java.util.UUID;

public interface StorageImplementation {

    List<User> getPlayers();

    User getPlayer(String name);

    User getPlayer(UUID uuid);

    void savePlayer(User player);

    User createPlayer(UUID uuid, String name);

    List<Kingdom> getKingdoms();

    void saveKingdom(Kingdom kingdom);

    Kingdom createKingdom(String name);

    void deleteKingdom(Kingdom kingdom);

    void saveRank(Rank rank);

    Rank createRank(String name, Kingdom kingdom);

    void deleteRank(Rank rank);

}
