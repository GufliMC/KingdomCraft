package com.igufguf.kingdomcraft.bukkit.domain;

import com.igufguf.kingdomcraft.api.KingdomCraftPlugin;
import com.igufguf.kingdomcraft.api.domain.Factory;
import com.igufguf.kingdomcraft.api.domain.Kingdom;
import com.igufguf.kingdomcraft.api.domain.Player;
import com.igufguf.kingdomcraft.api.domain.Rank;
import com.igufguf.kingdomcraft.common.kingdom.DefaultKingdom;
import com.igufguf.kingdomcraft.common.kingdom.DefaultRank;
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
