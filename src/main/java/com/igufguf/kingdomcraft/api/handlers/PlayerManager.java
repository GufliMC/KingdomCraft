package com.igufguf.kingdomcraft.api.handlers;


import com.igufguf.kingdomcraft.api.models.Kingdom;
import com.igufguf.kingdomcraft.api.models.Player;

import java.util.List;
import java.util.UUID;

public interface PlayerManager {

    public List<Player> getPlayers();

    public List<Player> getOnlinePlayers();

    public Player getPlayer(String name);

    public Player getOnlinePlayer(String name);

    public Player join(UUID id, String name);

    public void quit(Player player);

    public void joinKingdom(Player player, Kingdom kingdom);

    public void leaveKingdom(Player player);

}
