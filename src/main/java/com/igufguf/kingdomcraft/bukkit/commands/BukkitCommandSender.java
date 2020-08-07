package com.igufguf.kingdomcraft.bukkit.commands;

import com.igufguf.kingdomcraft.api.commands.CommandSender;
import com.igufguf.kingdomcraft.api.domain.Player;

public class BukkitCommandSender implements CommandSender {

    private org.bukkit.command.CommandSender commandSender;
    private Player player = null;

    public BukkitCommandSender(org.bukkit.command.CommandSender commandSender) {
        this.commandSender = commandSender;
    }

    public BukkitCommandSender(org.bukkit.command.CommandSender commandSender, Player player) {
        this.player = player;
        this.commandSender = commandSender;
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
