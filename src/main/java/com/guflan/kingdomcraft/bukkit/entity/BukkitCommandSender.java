package com.guflan.kingdomcraft.bukkit.entity;

import com.guflan.kingdomcraft.api.entity.CommandSender;

public class BukkitCommandSender implements CommandSender {

    protected final org.bukkit.command.CommandSender commandSender;

    public BukkitCommandSender(org.bukkit.command.CommandSender commandSender) {
        this.commandSender = commandSender;
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
