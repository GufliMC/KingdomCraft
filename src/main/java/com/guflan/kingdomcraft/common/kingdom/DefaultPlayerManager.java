package com.guflan.kingdomcraft.common.kingdom;

import com.guflan.kingdomcraft.api.KingdomCraftPlugin;
import com.guflan.kingdomcraft.api.entity.EntityPlayer;
import com.guflan.kingdomcraft.bukkit.entity.BukkitEntityPlayer;
import com.guflan.kingdomcraft.common.storage.Storage;
import com.guflan.kingdomcraft.api.domain.KingdomInvite;
import com.guflan.kingdomcraft.api.managers.PlayerManager;
import com.guflan.kingdomcraft.api.domain.Kingdom;
import com.guflan.kingdomcraft.api.domain.Player;
import org.bukkit.Bukkit;

import java.util.*;
import java.util.concurrent.ExecutionException;

public class DefaultPlayerManager implements PlayerManager {

    private final KingdomCraftPlugin plugin;
    private final Storage storage;

    protected final List<EntityPlayer> players = new ArrayList<>();

    public DefaultPlayerManager(KingdomCraftPlugin plugin, Storage storage) {
        this.plugin = plugin;
        this.storage = storage;
    }

    public List<EntityPlayer> getOnlinePlayers() {
        return new ArrayList<>(players);
    }

    public EntityPlayer getOnlinePlayer(String name) {
        return players.stream().filter(p -> p.getPlayer().getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public EntityPlayer getOnlinePlayer(UUID uuid) {
        return players.stream().filter(p -> p.getPlayer().getUniqueId() == uuid).findFirst().orElse(null);
    }

    @Override
    public void join(EntityPlayer player) {
        if ( players.contains(player) ) {
            return;
        }

        players.add(player);
        plugin.getEventManager().leave(player.getPlayer());
    }

    @Override
    public void leave(EntityPlayer player) {
        players.remove(player);
        plugin.getEventManager().leave(player.getPlayer());
    }

    // -------------

    public List<Player> getPlayers() {
        try {
            return storage.getPlayers().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public Player getPlayer(UUID uuid, String name) {
        try {
            Player player = storage.getPlayer(uuid).get();
            if ( player == null ) {
                player = storage.getPlayer(name).get();
            }

            if ( player == null ) {
                player = storage.createPlayer(uuid, name).get();
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

    public Player getPlayer(String name) {
        try {
            return storage.getPlayer(name).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Player getPlayer(UUID uuid) {
        try {
            return storage.getPlayer(uuid).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
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
        target.addInvite(from);
        savePlayer(target);
    }

    @Override
    public void savePlayer(Player player) {
        storage.savePlayer(player);
    }

}
