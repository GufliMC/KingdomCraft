package com.igufguf.kingdomcraft.bukkit.integration;

import com.igufguf.kingdomcraft.api.domain.Player;
import com.igufguf.kingdomcraft.api.integration.OnlinePlayer;

public class BukkitOnlinePlayer implements OnlinePlayer {

    private final org.bukkit.entity.Player bplayer;
    private final Player player;

    public BukkitOnlinePlayer(org.bukkit.entity.Player bplayer, Player player) {
        this.bplayer = bplayer;
        this.player = player;
    }


    @Override
    public void sendMessage(String msg) {
        this.bplayer.sendMessage(msg);
    }

    @Override
    public boolean hasPermission(String permission) {
        return this.bplayer.hasPermission(permission);
    }

    @Override
    public Player getPlayer() {
        return player;
    }
}
