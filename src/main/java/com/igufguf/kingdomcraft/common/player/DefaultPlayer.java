package com.igufguf.kingdomcraft.common.player;

import com.igufguf.kingdomcraft.api.domain.Kingdom;
import com.igufguf.kingdomcraft.api.domain.KingdomInvite;
import com.igufguf.kingdomcraft.api.domain.Player;
import com.igufguf.kingdomcraft.api.domain.Rank;
import org.apache.commons.lang.NotImplementedException;

import java.util.List;
import java.util.UUID;

public class DefaultPlayer implements Player {

    private final UUID uuid;
    private final String name;

    private Rank rank;

    private List<KingdomInvite> invites;

    private boolean adminMode;

    public DefaultPlayer(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    @Override
    public UUID getUniqueId() {
        return uuid;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Rank getRank() {
        return rank;
    }

    @Override
    public Kingdom getKingdom() {
        return rank != null ? rank.getKingdom() : null;
    }

    @Override
    public void setKingdom(Kingdom kingdom) {
        if ( kingdom == null ) {
            this.rank = null;
        } else {
            this.rank = kingdom.getDefaultRank();
        }
    }

    @Override
    public void setRank(Rank rank) {
        this.rank = rank;
    }

    @Override
    public List<KingdomInvite> getInvites() {
        return invites;
    }

    @Override
    public boolean isInvitedFor(Kingdom kingdom) {
        for ( KingdomInvite invite : invites ) {
            if ( invite.getKingdom() == kingdom && invite.getPlayer().getKingdom() == kingdom ) {
                return true; // TODO time check
            }
        }
        return false;
    }

    @Override
    public void addInvite(KingdomInvite invite) {
        this.invites.add(invite);
    }

    @Override
    public boolean hasAdminMode() {
        return adminMode;
    }

    @Override
    public void setAdminMode(boolean adminMode) {
        this.adminMode = adminMode;
    }

    //

    @Override
    public boolean hasPermission(String permission) {
        throw new NotImplementedException();
    }

    @Override
    public void sendMessage(String msg) {
        throw new NotImplementedException();
    }
}
