package com.igufguf.kingdomcraft.common.managers;

import com.igufguf.kingdomcraft.api.KingdomCraftPlugin;
import com.igufguf.kingdomcraft.api.domain.KingdomInvite;
import com.igufguf.kingdomcraft.api.managers.PlayerManager;
import com.igufguf.kingdomcraft.api.domain.Kingdom;
import com.igufguf.kingdomcraft.api.domain.Player;
import com.igufguf.kingdomcraft.common.domain.DefaultKingdomInvite;
import com.igufguf.kingdomcraft.common.domain.DefaultPlayer;
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

    public Player getOnlinePlayer(UUID uuid) {
        return players.stream().filter(p -> p.getUniqueId() == uuid).findFirst().orElse(null);
    }

    @Override
    public Player load(UUID uuid, String name) {
        try {
            Player player = storage.getPlayer(uuid).get();
            if ( player == null ) {
                player = storage.getPlayer(name).get();
            }

            if ( player == null ) {
                player = new DefaultPlayer(uuid, name);
                savePlayer(player);
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

    @Override
    public void unload(Player player) {
        players.remove(player);
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

    @Override
    public Player getPlayer(UUID uuid) {
        Player player = getOnlinePlayer(uuid);
        if ( player != null ) {
            return player;
        }

        try {
            player = storage.getPlayer(uuid).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return player;
    }

    public void joinKingdom(Player player, Kingdom kingdom) {
        player.setKingdom(kingdom);
        storage.savePlayer(player);
    }

    public void leaveKingdom(Player player) {
        player.setKingdom(null);
        storage.savePlayer(player);
    }

    @Override
    public void addInvite(Player from, Player target) {
        KingdomInvite invite = new DefaultKingdomInvite(from, target, from.getKingdom());
        target.addInvite(invite);
        savePlayer(target);
    }

    @Override
    public void savePlayer(Player player) {
        storage.savePlayer(player);
    }

}
