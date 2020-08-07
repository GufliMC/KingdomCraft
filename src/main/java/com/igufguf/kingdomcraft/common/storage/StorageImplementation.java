package com.igufguf.kingdomcraft.common.storage;


import com.igufguf.kingdomcraft.api.domain.Kingdom;
import com.igufguf.kingdomcraft.api.domain.Player;
import com.igufguf.kingdomcraft.api.domain.Rank;

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
