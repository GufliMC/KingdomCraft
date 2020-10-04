package com.guflan.kingdomcraft.api.entity;

public interface CommandSender {

    boolean hasPermission(String permission);

    void sendMessage(String msg);

}
