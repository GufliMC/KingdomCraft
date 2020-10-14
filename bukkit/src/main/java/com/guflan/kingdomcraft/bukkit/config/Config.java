/*
 * This file is part of KingdomCraft.
 *
 * KingdomCraft is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * KingdomCraft is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with KingdomCraft. If not, see <https://www.gnu.org/licenses/>.
 */

package com.guflan.kingdomcraft.bukkit.config;

import com.guflan.kingdomcraft.api.domain.models.RelationType;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Config {

    private final List<String> worlds;
    private final List<RelationType> friendlyFireRelationTypes;

    private final boolean respawnAtKingdom;

    private final List<String> onKingdomJoinCommands;
    private final List<String> onKingdomLeaveCommands;

    private final String onJoinMessage;
    private final String onLeaveMessage;
    private final String onDeathMessage;
    private final String onKillMessage;

    public Config(ConfigurationSection cs) {

        if ( cs.contains("worlds") ) {
            worlds = cs.getStringList("worlds").stream().map(String::toLowerCase).collect(Collectors.toList());
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

        if ( cs.contains("respawn_at_kingdom") ) {
            respawnAtKingdom = cs.getBoolean("respawn_at_kingdom");
        } else {
            respawnAtKingdom = false;
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

    public List<String> getWorlds() {
        return worlds;
    }

    public boolean isWorldEnabled(String world) {
        return worlds.contains(world.toLowerCase());
    }

    public List<RelationType> getFriendlyFireRelationTypes() {
        return friendlyFireRelationTypes;
    }

    public List<String> getOnKingdomJoinCommands() {
        return onKingdomJoinCommands;
    }

    public List<String> getOnKingdomLeaveCommands() {
        return onKingdomLeaveCommands;
    }

    public boolean respawnAtKingdom() {
        return respawnAtKingdom;
    }

    public String getOnJoinMessage() {
        return onJoinMessage;
    }

    public String getOnLeaveMessage() {
        return onLeaveMessage;
    }

    public String getOnDeathMessage() {
        return onDeathMessage;
    }

    public String getOnKillMessage() {
        return onKillMessage;
    }
}
