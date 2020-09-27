package com.guflan.kingdomcraft.bukkit.domain;

import com.guflan.kingdomcraft.api.KingdomCraftPlugin;
import com.guflan.kingdomcraft.common.kingdom.DefaultKingdom;
import com.guflan.kingdomcraft.common.kingdom.DefaultRank;
import com.guflan.kingdomcraft.api.domain.Factory;
import com.guflan.kingdomcraft.api.domain.Kingdom;
import com.guflan.kingdomcraft.api.domain.Player;
import com.guflan.kingdomcraft.api.domain.Rank;
import org.bukkit.Bukkit;

import java.util.UUID;

public class BukkitFactory implements Factory {

    public BukkitFactory(KingdomCraftPlugin plugin) {

    }

    @Override
    public Player createPlayer(UUID uuid, String name) {
        org.bukkit.entity.Player bplayer = Bukkit.getPlayer(uuid);
        return new BukkitPlayer(bplayer);
    }

    @Override
    public Kingdom createKingdom(String name) {
        return new DefaultKingdom(name);
    }

    @Override
    public Rank createRank(String name, Kingdom kingdom) {
        return new DefaultRank(name, kingdom);
    }
}
