package com.guflan.kingdomcraft.bukkit.entity;

import com.guflan.kingdomcraft.api.domain.Player;
import com.guflan.kingdomcraft.api.entity.CommandSender;

public class BukkitCommandSender implements CommandSender {

    private final org.bukkit.command.CommandSender commandSender;
    private Player player;

    public BukkitCommandSender(org.bukkit.command.CommandSender commandSender) {
        this.commandSender = commandSender;
    }

    public BukkitCommandSender(org.bukkit.command.CommandSender commandSender, Player player) {
        this(commandSender);
        this.player = player;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public boolean hasPermission(String permission) {
        return commandSender.hasPermission(permission);
    }

    @Override
    public void sendMessage(String msg) {
        commandSender.sendMessage(msg);
    }

}
