package com.igufguf.kingdomcraft.api.domain;

import java.util.UUID;

public interface Factory {

    Player createPlayer(UUID uuid, String name);

    Kingdom createKingdom(String name);

    Rank createRank(String name, Kingdom kingdom);

}
