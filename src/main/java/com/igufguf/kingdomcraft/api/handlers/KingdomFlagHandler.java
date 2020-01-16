package com.igufguf.kingdomcraft.api.handlers;

import com.igufguf.kingdomcraft.api.models.kingdom.Kingdom;
import com.igufguf.kingdomcraft.api.models.flags.KingdomFlag;

import java.util.Collection;
import java.util.List;
import java.util.Map;

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

    Map<KingdomFlag, Object> getFlags(Kingdom kd);

    void setFlag(Kingdom kd, KingdomFlag flag, Object value);

    boolean hasFlag(Kingdom kd, KingdomFlag flag);
    <T> T getFlag(Kingdom kd, KingdomFlag<T> flag);
}
