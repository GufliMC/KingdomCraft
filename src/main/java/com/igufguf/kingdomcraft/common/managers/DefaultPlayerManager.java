package com.igufguf.kingdomcraft.common.managers;

import com.igufguf.kingdomcraft.api.KingdomCraftPlugin;
import com.igufguf.kingdomcraft.api.managers.PlayerManager;
import com.igufguf.kingdomcraft.api.models.Kingdom;
import com.igufguf.kingdomcraft.api.models.Player;
import com.igufguf.kingdomcraft.common.storage.Storage;

import java.util.*;
import java.util.concurrent.ExecutionException;

public class DefaultPlayerManager implements PlayerManager {

    private final KingdomCraftPlugin plugin;
    private final Storage storage;

    private List<Player> players;

    public DefaultPlayerManager(KingdomCraftPlugin plugin, Storage storage) {
        this.plugin = plugin;
        this.storage = storage;
    }

    public List<Player> getOnlinePlayers() {
        return new ArrayList<>(players);
    }

    public List<Player> getPlayers() {
        try {
            return storage.getPlayers().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public Player getOnlinePlayer(String name) {
        return players.stream().filter(p -> p.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public Player getPlayer(String name) {
        Player player = getOnlinePlayer(name);
        if ( player != null ) {
            return player;
        }

        try {
            player = storage.getPlayer(name).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return player;
    }

    public Player join(UUID uuid, String name) {
        try {
            Player player = storage.getPlayer(uuid).get();
            if ( player == null ) {
                player = storage.getPlayer(name).get();
            }

            if ( player == null ) {
                // TODO create new player
            }
            else if ( !player.getName().equals(name) ) {
                // TODO update name
            }

            return player;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void quit(Player player) {
        players.remove(player);
    }

    public void joinKingdom(Player player, Kingdom kingdom) {
        player.setKingdom(kingdom);
        storage.savePlayer(player);
    }

    public void leaveKingdom(Player player) {
        player.setKingdom(null);
        storage.savePlayer(player);
    }

}
