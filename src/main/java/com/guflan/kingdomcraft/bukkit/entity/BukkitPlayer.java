package com.guflan.kingdomcraft.bukkit.entity;

import com.guflan.kingdomcraft.api.entity.Player;

import java.util.UUID;

public class BukkitPlayer extends BukkitCommandSender implements Player {

    private org.bukkit.entity.Player player;

    public BukkitPlayer(org.bukkit.entity.Player player) {
        super(player);
        this.player = player;
    }

    @Override
    public UUID getUniqueId() {
        return this.player.getUniqueId();
    }

    @Override
    public String getName() {
        return this.player.getName();
    }

}
