package com.guflan.kingdomcraft.api.domain.models;

public interface Relation {

    Kingdom getKingdom1();

    Kingdom getKingdom2();

    RelationType getType();

    boolean isRequest();

}
