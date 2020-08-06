package com.igufguf.kingdomcraft.common.storage;


import com.igufguf.kingdomcraft.api.models.Kingdom;
import com.igufguf.kingdomcraft.api.models.Player;
import com.igufguf.kingdomcraft.api.models.Rank;

import java.util.List;
import java.util.UUID;

public interface StorageImplementation {

    List<Player> getPlayers();

    Player getPlayer(String name);

    Player getPlayer(UUID uuid);

    void savePlayer(Player player);

    List<Kingdom> getKingdoms();

    void saveKingdom(Kingdom kingdom);

    void saveRank(Rank rank);

    void deleteKingdom(Kingdom kingdom);

}
