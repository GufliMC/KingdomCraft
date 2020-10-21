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

package com.guflan.kingdomcraft.common.config;

import com.guflan.kingdomcraft.api.domain.RelationType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class KingdomCraftConfig {

    private final Configuration config;

    public KingdomCraftConfig(Configuration config) {
        this.config = config;
    }

    public int getTeleportDelay() {
        return config.contains("teleport-delay") ? config.getInt("teleport-delay") : 0;
    }

    public List<String> getWorlds() {
        return config.contains("worlds") ? config.getStringList("worlds") : new ArrayList<>();
    }

    public boolean isWorldEnabled(String world) {
        List<String> worlds = getWorlds();
        return worlds.isEmpty() || worlds.contains(world.toLowerCase());
    }

    public List<RelationType> getFriendlyFireRelationTypes() {
        return config.contains("friendly-fire-relationships") ?
                config.getStringList("friendly-fire-relationships").stream()
                        .map(s -> RelationType.valueOf(s.toUpperCase())).collect(Collectors.toList())
                : new ArrayList<>();
    }

    public List<String> getOnKingdomJoinCommands() {
        return config.contains("events.kingdom_join") ? config.getStringList("events.kingdom_join") : new ArrayList<>();
    }

    public List<String> getOnKingdomLeaveCommands() {
        return config.contains("events.kingdom_leave") ? config.getStringList("events.kingdom_leave") : new ArrayList<>();
    }

    public boolean respawnAtKingdom() {
        return config.contains("respawn-at-kingdom") && config.getBoolean("respawn-at-kingdom");
    }

    public String getOnJoinMessage() {
        return config.contains("messages.join") ? config.getString("messages.join") : null;
    }

    public String getOnLeaveMessage() {
        return config.contains("messages.leave") ? config.getString("messages.leave") : null;
    }

    public String getOnDeathMessage() {
        return config.contains("messages.death") ? config.getString("messages.death") : null;
    }

    public String getOnKillMessage() {
        return config.contains("messages.kill") ? config.getString("messages.kill") : null;
    }

    public String getNoKingdomPrefix() {
        return config.contains("nokingdom.prefix") ? config.getString("nokingdom.prefix") : "";
    }

    public String getNoKingdomSuffix() {
        return config.contains("nokingdom.suffix") ? config.getString("nokingdom.suffix") : "";
    }
}
