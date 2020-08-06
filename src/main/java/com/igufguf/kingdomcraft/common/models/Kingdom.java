package com.igufguf.kingdomcraft.common.models;

import org.bukkit.Location;

import java.util.List;
import java.util.Map;

public class Kingdom {

    String name;

    String display;
    String prefix;
    String suffix;

    String spawn;

    boolean inviteOnly = false;

    List<Rank> ranks;
    Rank defaultRank;

    Map<Kingdom, Relation> relations;

    public Kingdom(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public Location getSpawn() {
        return null; // TODO
    }

    public void setSpawn(Location spawn) {
        // TODO
    }

    public boolean isInviteOnly() {
        return inviteOnly;
    }

    public void setInviteOnly(boolean inviteOnly) {
        this.inviteOnly = inviteOnly;
    }

    public List<Rank> getRanks() {
        return ranks;
    }

    public Rank getDefaultRank() {
        return defaultRank;
    }

    public void addRank(Rank rank) {
        if (rank.kingdom != this) {
            throw new RuntimeException("This rank does not belong to this kingdom."); // TODO
        }

        if (this.ranks.contains(rank)) {
            return;
        }

        this.ranks.add(rank);
    }

    public void removeRank(Rank rank) {
        this.ranks.remove(rank);
    }

    public void setDefaultRank(Rank defaultRank) {
        this.defaultRank = defaultRank;
    }
}
