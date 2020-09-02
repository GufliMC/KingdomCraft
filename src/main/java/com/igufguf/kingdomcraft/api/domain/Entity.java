package com.igufguf.kingdomcraft.api.domain;

public interface Entity {

    boolean hasPermission(String permission);

    void sendMessage(String msg);

}
