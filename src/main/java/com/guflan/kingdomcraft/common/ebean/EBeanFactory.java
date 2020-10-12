package com.guflan.kingdomcraft.common.ebean;

import com.guflan.kingdomcraft.api.domain.DomainFactory;
import com.guflan.kingdomcraft.api.domain.models.Kingdom;
import com.guflan.kingdomcraft.api.domain.models.Relation;
import com.guflan.kingdomcraft.api.domain.models.RelationType;
import com.guflan.kingdomcraft.api.domain.models.User;
import com.guflan.kingdomcraft.common.ebean.beans.BKingdom;
import com.guflan.kingdomcraft.common.ebean.beans.BRelation;
import com.guflan.kingdomcraft.common.ebean.beans.BUser;

import java.util.UUID;

public class EBeanFactory implements DomainFactory  {

    @Override
    public Kingdom createKingdom(String name) {
        BKingdom kingdom = new BKingdom();
        kingdom.name = name;
        return kingdom;
    }

    @Override
    public Relation createRelation(Kingdom kingdom, Kingdom other, RelationType type, boolean isRequest) {
        BRelation rel = new BRelation();
        rel.kingdom = (BKingdom) kingdom;
        rel.otherKingdom = (BKingdom) other;
        rel.relation = type.getId();
        rel.isRequest = isRequest;
        return rel;
    }

    @Override
    public User createUser(UUID uuid, String name) {
        BUser user = new BUser();
        user.id = uuid.toString();
        user.name = name;
        return user;
    }
}
