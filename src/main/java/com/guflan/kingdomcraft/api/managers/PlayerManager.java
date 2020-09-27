package com.guflan.kingdomcraft.api.managers;


import com.guflan.kingdomcraft.api.domain.Kingdom;
import com.guflan.kingdomcraft.api.domain.Player;

import java.util.List;
import java.util.UUID;

public interface PlayerManager {

    List<Player> getPlayers();

    List<Player> getOnlinePlayers();

    Player getPlayer(String name);

    Player getPlayer(UUID uuid);

    Player getOnlinePlayer(UUID uuid);

    Player getOnlinePlayer(String name);

    Player join(UUID id, String name);

    void leave(Player player);

    void joinKingdom(Player player, Kingdom kingdom);

    void leaveKingdom(Player player);

    void addInvite(Player from, Player target);

    void savePlayer(Player player);

}
