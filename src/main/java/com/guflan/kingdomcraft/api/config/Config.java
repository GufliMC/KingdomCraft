package com.guflan.kingdomcraft.api.config;

import com.guflan.kingdomcraft.api.domain.models.RelationType;

import java.util.List;

public interface Config {

    public List<String> getWorlds();

    List<RelationType> getFriendlyFireRelationTypes();

    List<String> getOnKingdomJoinCommands();

    List<String> getOnKingdomLeaveCommands();

    List<String> getOnRespawnCommands();

    String getOnJoinMessage();

    String getOnLeaveMessage();

    String getOnDeathMessage();

    String getOnKillMessage();

}
