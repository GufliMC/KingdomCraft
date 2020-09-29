package com.guflan.kingdomcraft.api.managers;


import com.guflan.kingdomcraft.api.domain.Kingdom;
import com.guflan.kingdomcraft.api.domain.Player;
import com.guflan.kingdomcraft.api.entity.EntityPlayer;

import java.util.List;
import java.util.UUID;

public interface PlayerManager {

    List<EntityPlayer> getOnlinePlayers();

    EntityPlayer getOnlinePlayer(UUID uuid);

    EntityPlayer getOnlinePlayer(String name);

    void join(EntityPlayer player);

    void leave(EntityPlayer player);

    //

    List<Player> getPlayers();

    Player getPlayer(UUID uuid, String name);

    Player getPlayer(String name);

    Player getPlayer(UUID uuid);

    void joinKingdom(Player player, Kingdom kingdom);

    void leaveKingdom(Player player);

    void addInvite(Player from, Player target);

    void savePlayer(Player player);

}
