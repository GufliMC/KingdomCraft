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

public abstract class KingdomCraftConfig {

    protected List<String> worlds;
    protected List<RelationType> friendlyFireRelationTypes;

    protected boolean respawnAtKingdom;

    protected List<String> onKingdomJoinCommands;
    protected List<String> onKingdomLeaveCommands;

    protected String onJoinMessage;
    protected String onLeaveMessage;
    protected String onDeathMessage;
    protected String onKillMessage;

    public List<String> getWorlds() {
        return worlds;
    }

    public boolean isWorldEnabled(String world) {
        return worlds.isEmpty() || worlds.contains(world.toLowerCase());
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
