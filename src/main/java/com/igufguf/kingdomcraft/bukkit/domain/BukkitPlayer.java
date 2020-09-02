package com.igufguf.kingdomcraft.bukkit.domain;

import com.igufguf.kingdomcraft.common.player.DefaultPlayer;
import org.bukkit.entity.Player;

public class BukkitPlayer extends DefaultPlayer {

    private Player bplayer;

    public BukkitPlayer(Player bplayer) {
        super(bplayer.getUniqueId(), bplayer.getName());
        this.bplayer = bplayer;
    }

    @Override
    public void sendMessage(String msg) {
        bplayer.sendMessage(msg);
    }

    @Override
    public boolean hasPermission(String permission) {
        return bplayer.hasPermission(permission);
    }

}
