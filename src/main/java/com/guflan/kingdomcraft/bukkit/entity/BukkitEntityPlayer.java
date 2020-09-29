package com.guflan.kingdomcraft.bukkit.entity;

import com.guflan.kingdomcraft.api.domain.Player;
import com.guflan.kingdomcraft.api.entity.EntityPlayer;

public class BukkitEntityPlayer extends BukkitCommandSender implements EntityPlayer {

    private boolean admin = false;

    public BukkitEntityPlayer(org.bukkit.entity.Player bplayer, Player player) {
        super(bplayer, player);
    }

    @Override
    public boolean isAdmin() {
        return admin;
    }

    @Override
    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
}
