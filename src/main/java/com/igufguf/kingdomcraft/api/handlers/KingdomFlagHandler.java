package com.igufguf.kingdomcraft.api.handlers;

import com.igufguf.kingdomcraft.api.models.kingdom.Kingdom;
import com.igufguf.kingdomcraft.api.models.flags.KingdomFlag;

import java.util.Collection;
import java.util.List;

/**
 * Created by Joris on 17/08/2018 in project KingdomCraft.
 */
public interface KingdomFlagHandler {

    // flag registry

    void register(KingdomFlag flag);

    void registerAll(Collection<KingdomFlag> flags);
    void registerAll(KingdomFlag... flags);

    KingdomFlag getFlag(String name);

    List<KingdomFlag> getAllFlags();

    // kingdoms

    List<KingdomFlag> getFlags(Kingdom kd);

    void setFlag(Kingdom kd, KingdomFlag flag, Object value);
    //Object getFlagValue(Kingdom kd, KingdomFlag flag);

    boolean hasFlag(Kingdom kd, KingdomFlag flag);
    <T> T getFlagValue(Kingdom kd, KingdomFlag<T> flag);
}
