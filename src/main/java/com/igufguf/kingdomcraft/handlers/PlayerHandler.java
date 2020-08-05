package com.igufguf.kingdomcraft.handlers;

import com.igufguf.kingdomcraft.KingdomCraft;
import com.igufguf.kingdomcraft.domain.Player;
import com.igufguf.kingdomcraft.domain.query.QPlayer;
import io.ebean.Database;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerHandler {

    private final KingdomCraft kingdomCraft;
    private final Database database;

    private final Map<org.bukkit.entity.Player, Player> players = new HashMap<>();

    public PlayerHandler(KingdomCraft kingdomCraft, Database database) {
        this.kingdomCraft = kingdomCraft;
        this.database = database;
    }

    public List<Player> getOnlinePlayers() {
        return new ArrayList<>(players.values());
    }

    List<Player> getPlayers() {
        return database.find(Player.class).findList();
    }

    public Player getPlayer(org.bukkit.entity.Player bplayer) {
        return players.get(bplayer);
    }

    public Player getPlayer(String name) {
        Player player = getPlayer(Bukkit.getPlayer(name));
        if ( player != null ) {
            return player;
        }

        player = new QPlayer(database).name.eq(name).findOne();
        return player;
    }

    public void join(org.bukkit.entity.Player bplayer) {
        Player player = new QPlayer(database).id.equalTo(bplayer.getUniqueId().toString()).findOne();

        if (player == null) {
            player = new Player(bplayer.getUniqueId(), bplayer.getName());
            database.save(player);
            return;
        }

        if (!player.getName().equals(bplayer.getName())) {
            player.setName(bplayer.getName());
        }
    }

    public void quit(org.bukkit.entity.Player player) {
        players.remove(player);
    }

}
