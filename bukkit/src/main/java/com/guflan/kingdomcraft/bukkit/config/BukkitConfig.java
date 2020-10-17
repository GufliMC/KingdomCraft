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

import com.guflan.kingdomcraft.api.domain.RelationType;
import com.guflan.kingdomcraft.common.config.KingdomCraftConfig;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class BukkitConfig extends KingdomCraftConfig  {

    public BukkitConfig(ConfigurationSection cs) {

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

        if ( cs.contains("respawn-at-kingdom") ) {
            respawnAtKingdom = cs.getBoolean("respawn-at-kingdom");
        } else {
            respawnAtKingdom = false;
        }

        if ( cs.contains("teleport-delay") ) {
            teleportDelay = cs.getInt("teleport-delay");
        } else {
            teleportDelay = 0;
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
}
