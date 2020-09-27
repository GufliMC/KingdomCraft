package com.guflan.kingdomcraft.bukkit.command;

import com.guflan.kingdomcraft.api.command.CommandSender;
import com.guflan.kingdomcraft.api.domain.Player;

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
    public void sendMessage(String msg) {
        commandSender.sendMessage(msg);
    }

    @Override
    public boolean hasPermission(String permission) {
        return commandSender.hasPermission(permission);
    }

    @Override
    public Player getPlayer() {
        return player;
    }

}
