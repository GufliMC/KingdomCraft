package com.igufguf.kingdomcraft.api.managers;


import com.igufguf.kingdomcraft.api.domain.Kingdom;
import com.igufguf.kingdomcraft.api.domain.Player;

import java.util.List;
import java.util.UUID;

public interface PlayerManager {

    public List<Player> getPlayers();

    public List<Player> getOnlinePlayers();

    public Player getPlayer(String name);

    public Player getPlayer(UUID uuid);

    public Player getOnlinePlayer(String name);

    public Player load(UUID id, String name);

    public void unload(Player player);

    public void joinKingdom(Player player, Kingdom kingdom);

    public void leaveKingdom(Player player);

    public void addInvite(Player from, Player target);

    public void savePlayer(Player player);

}
