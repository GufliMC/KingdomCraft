package com.guflan.kingdomcraft.common.storage;


import com.guflan.kingdomcraft.api.domain.Kingdom;
import com.guflan.kingdomcraft.api.domain.Player;
import com.guflan.kingdomcraft.api.domain.Rank;

import java.util.List;
import java.util.UUID;

public interface StorageImplementation {

    List<Player> getPlayers();

    Player getPlayer(String name);

    Player getPlayer(UUID uuid);

    void savePlayer(Player player);

    Player createPlayer(UUID uuid, String name);

    List<Kingdom> getKingdoms();

    void saveKingdom(Kingdom kingdom);

    Kingdom createKingdom(String name);

    void deleteKingdom(Kingdom kingdom);

    void saveRank(Rank rank);

    Rank createRank(String name, Kingdom kingdom);

    void deleteRank(Rank rank);

}
