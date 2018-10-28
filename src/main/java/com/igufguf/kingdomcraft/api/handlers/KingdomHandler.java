package com.igufguf.kingdomcraft.api.handlers;

import com.igufguf.kingdomcraft.api.models.kingdom.Kingdom;
import com.igufguf.kingdomcraft.api.models.kingdom.KingdomRank;
import com.igufguf.kingdomcraft.api.models.kingdom.KingdomUser;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Created by Joris on 17/08/2018 in project KingdomCraft.
 */
public interface KingdomHandler {

    // kingdom getters

    List<Kingdom> getKingdoms();

    Kingdom getKingdom(String name);

    // get kingdom members

    List<Player> getOnlineMembers(Kingdom kingdom);

    List<KingdomUser> getMembers(Kingdom kingdom);

    // manage

    Kingdom load(String name);
    void save(Kingdom ko);

    // api, retrieve custom kingdom or rank options
    Object getKingdomConfigOption(Kingdom kingdom, String option);
    Object getRankConfigOption(KingdomRank rank, String option);

    boolean containsKingdomConfigOption(Kingdom kingdom, String option);
    boolean containsRankConfigOption(KingdomRank rank, String option);

}
