package com.igufguf.kingdomcraft.api.handlers;

import com.igufguf.kingdomcraft.api.models.kingdom.Kingdom;
import com.igufguf.kingdomcraft.api.models.kingdom.KingdomRelation;

import java.util.HashMap;

/**
 * Created by Joris on 17/08/2018 in project KingdomCraft.
 */
public interface KingdomRelationHandler {

    KingdomRelation getRelation(Kingdom kingdom, Kingdom target);

    void setRelation(Kingdom kingdom, Kingdom target, KingdomRelation relation);

    HashMap<Kingdom, KingdomRelation> getRelations(Kingdom kingdom);

}
