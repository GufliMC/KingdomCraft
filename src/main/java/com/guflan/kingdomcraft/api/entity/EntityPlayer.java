package com.guflan.kingdomcraft.api.entity;

public interface EntityPlayer extends CommandSender {

    boolean isAdmin();

    void setAdmin(boolean admin);

}
