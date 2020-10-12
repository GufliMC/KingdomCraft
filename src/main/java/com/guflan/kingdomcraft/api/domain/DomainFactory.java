package com.guflan.kingdomcraft.api.domain;

import com.guflan.kingdomcraft.api.domain.models.Kingdom;
import com.guflan.kingdomcraft.api.domain.models.Relation;
import com.guflan.kingdomcraft.api.domain.models.RelationType;
import com.guflan.kingdomcraft.api.domain.models.User;

import java.util.UUID;

public interface DomainFactory {

    Kingdom createKingdom(String name);

    Relation createRelation(Kingdom kingdom, Kingdom other, RelationType type, boolean isRequest);

    User createUser(UUID uuid, String name);

}
