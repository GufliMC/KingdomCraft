package com.guflan.kingdomcraft.bukkit.config;

import com.guflan.kingdomcraft.api.config.Config;
import com.guflan.kingdomcraft.api.domain.models.RelationType;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BukkitConfig implements Config {

    private final List<String> worlds;
    private final List<RelationType> friendlyFireRelationTypes;

    private final List<String> onKingdomJoinCommands;
    private final List<String> onKingdomLeaveCommands;
    private final List<String> onRespawnCommands;

    private final String onJoinMessage;
    private final String onLeaveMessage;
    private final String onDeathMessage;
    private final String onKillMessage;

    public BukkitConfig(ConfigurationSection cs) {

        if ( cs.contains("worlds") ) {
            worlds = cs.getStringList("worlds");
        } else {
            worlds = new ArrayList<>();
        }

        if ( cs.contains("friendly-fire-relationships") ) {
            friendlyFireRelationTypes = cs.getStringList("friendly-fire-relationships").stream()
                    .map(s -> RelationType.valueOf(s.toUpperCase())).collect(Collectors.toList());
        } else {
            friendlyFireRelationTypes = new ArrayList<>();
        }

        if ( cs.contains("events.kingdom_join") ) {
            onKingdomJoinCommands = cs.getStringList("events.kingdom_join");
        } else {
            onKingdomJoinCommands = new ArrayList<>();
        }

        if ( cs.contains("events.kingdom_leave") ) {
            onKingdomLeaveCommands = cs.getStringList("events.kingdom_leave");
        } else {
            onKingdomLeaveCommands = new ArrayList<>();
        }

        if ( cs.contains("events.respawn") ) {
            onRespawnCommands = cs.getStringList("events.respawn");
        } else {
            onRespawnCommands = new ArrayList<>();
        }

        if ( cs.contains("messages.join") ) {
            onJoinMessage = cs.getString("messages.join");
        } else {
            onJoinMessage = null;
        }

        if ( cs.contains("messages.leave") ) {
            onLeaveMessage = cs.getString("messages.leave");
        } else {
            onLeaveMessage = null;
        }

        if ( cs.contains("messages.death") ) {
            onDeathMessage = cs.getString("messages.death");
        } else {
            onDeathMessage = null;
        }

        if ( cs.contains("messages.kill") ) {
            onKillMessage = cs.getString("messages.kill");
        } else {
            onKillMessage = null;
        }

    }

    @Override
    public List<String> getWorlds() {
        return worlds;
    }

    @Override
    public List<RelationType> getFriendlyFireRelationTypes() {
        return friendlyFireRelationTypes;
    }

    @Override
    public List<String> getOnKingdomJoinCommands() {
        return onKingdomJoinCommands;
    }

    @Override
    public List<String> getOnKingdomLeaveCommands() {
        return onKingdomLeaveCommands;
    }

    @Override
    public List<String> getOnRespawnCommands() {
        return onRespawnCommands;
    }

    @Override
    public String getOnJoinMessage() {
        return onJoinMessage;
    }

    @Override
    public String getOnLeaveMessage() {
        return onLeaveMessage;
    }

    @Override
    public String getOnDeathMessage() {
        return onDeathMessage;
    }

    @Override
    public String getOnKillMessage() {
        return onKillMessage;
    }
}
