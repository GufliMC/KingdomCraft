package com.guflan.kingdomcraft.api.entity;

import java.util.UUID;

public interface Player extends CommandSender {

    UUID getUniqueId();

    String getName();

}
