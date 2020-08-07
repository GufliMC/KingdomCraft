package com.igufguf.kingdomcraft.common.domain;

import com.igufguf.kingdomcraft.api.domain.Kingdom;
import com.igufguf.kingdomcraft.api.domain.KingdomInvite;
import com.igufguf.kingdomcraft.api.domain.Player;

import java.util.Date;

public class DefaultKingdomInvite implements KingdomInvite  {

    private final Player sender;
    private final Player player;
    private final Kingdom kingdom;

    private Date createdAt;

    public DefaultKingdomInvite(Player sender, Player player, Kingdom kingdom) {
        this.sender = sender;
        this.player = player;
        this.kingdom = kingdom;
        this.createdAt = new Date();
    }

    public DefaultKingdomInvite(Player sender, Player player, Kingdom kingdom, Date createdAt) {
        this(sender, player, kingdom);
        this.createdAt = createdAt;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public Player getSender() {
        return sender;
    }

    @Override
    public Kingdom getKingdom() {
        return kingdom;
    }

    @Override
    public Date getCreatedAt() {
        return createdAt;
    }
}
