package com.igufguf.kingdomcraft.api.handlers;


import com.igufguf.kingdomcraft.api.models.Kingdom;
import com.igufguf.kingdomcraft.api.models.Player;

import java.util.List;

public interface KingdomManager {

    List<Kingdom> getKingdoms();

    Kingdom getKingdom(String name);

    Kingdom createKingdom(String name);

    void deleteKingdom(Kingdom kingdom);

    void saveKingdom(Kingdom kingdom);

    List<Player> getOnlineMembers(Kingdom kingdom);
}
