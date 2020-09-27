package com.guflan.kingdomcraft.common.player;

import com.guflan.kingdomcraft.api.KingdomCraftPlugin;
import com.guflan.kingdomcraft.common.kingdom.DefaultKingdomInvite;
import com.guflan.kingdomcraft.common.storage.Storage;
import com.guflan.kingdomcraft.api.domain.KingdomInvite;
import com.guflan.kingdomcraft.api.managers.PlayerManager;
import com.guflan.kingdomcraft.api.domain.Kingdom;
import com.guflan.kingdomcraft.api.domain.Player;

import java.util.*;
import java.util.concurrent.ExecutionException;

public class DefaultPlayerManager implements PlayerManager {

    private final KingdomCraftPlugin plugin;
    private final Storage storage;

    private final List<Player> players = new ArrayList<>();

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
    public Player join(UUID uuid, String name) {
        try {
            Player player = storage.getPlayer(uuid).get();
            if ( player == null ) {
                player = storage.getPlayer(name).get();
            }

            if ( player == null ) {
                player = plugin.getFactory().createPlayer(uuid, name);
            }
            else if ( !player.getName().equals(name) ) {
                // TODO update name
            }

            this.players.add(player);
            plugin.getEventManager().join(player);
            return player;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void leave(Player player) {
        players.remove(player);
        plugin.getEventManager().leave(player);
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
