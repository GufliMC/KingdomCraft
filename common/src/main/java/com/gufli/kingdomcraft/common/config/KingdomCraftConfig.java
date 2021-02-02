/*
 * This file is part of KingdomCraft.
 *
 * KingdomCraft is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * KingdomCraft is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with KingdomCraft. If not, see <https://www.gnu.org/licenses/>.
 */

package com.gufli.kingdomcraft.common.config;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

public class KingdomCraftConfig {

    private Configuration config;

    public KingdomCraftConfig(Configuration config) {
        this.config = config;
    }

    public void reload(Configuration config) {
        this.config = config;
    }

    public int getTeleportDelay() {
        return config.contains("teleport-delay") ? config.getInt("teleport-delay") : 0;
    }

    public List<String> getWorlds() {
        return config.contains("worlds") ? config.getStringList("worlds") : new ArrayList<>();
    }

    public ZoneId getTimeZone() {
        if ( config.contains("timezone") ) {
            try {
                return ZoneId.of(config.getString("timezone"));
            } catch (Exception ignored) {}
        }
        return ZoneId.systemDefault();
    }

    public DateTimeFormatter getDateFormat() {
        if ( config.contains("date-format") ) {
            try {
                return DateTimeFormatter.ofPattern(config.getString("date-format"));
            } catch (Exception ignored) {}
        }
        return DateTimeFormatter.ofPattern("yyyy-MM-dd");
    }

    public DateTimeFormatter getTimeFormat() {
        if ( config.contains("time-format") ) {
            try {
                return DateTimeFormatter.ofPattern(config.getString("time-format"));
            } catch (Exception ignored) {}
        }
        return DateTimeFormatter.ofPattern("HH:mm");
    }

    public DateTimeFormatter getDateTimeFormat() {
        String dateFormat = config.contains("date-format") ? config.getString("date-format") : "yyyy-MM-dd";
        String timeFormat = config.contains("time-format") ? config.getString("time-format") : "HH:mm";
        try {
            return DateTimeFormatter.ofPattern(dateFormat + " " + timeFormat);
        } catch (Exception ignored) {}
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    }

    public boolean isWorldEnabled(String world) {
        List<String> worlds = getWorlds();
        return worlds.isEmpty() || worlds.stream().anyMatch(s -> s.equalsIgnoreCase(world));
    }

    public List<String> getFriendlyFireRelationTypes() {
        return config.contains("friendly-fire-relationships") ?
                config.getStringList("friendly-fire-relationships").stream()
                        .map(String::toUpperCase).collect(Collectors.toList())
                : new ArrayList<>();
    }

    public boolean isFriendlyFireEnabled() {
        return config.contains("friendly-fire") && config.getBoolean("friendly-fire");
    }

    public List<String> getOnKingdomJoinCommands() {
        if ( config.contains("events.kingdom-join") ) {
            return config.getStringList("events.kingdom-join");
        } else if ( config.contains("events.kingdom_join") ) {
            return config.getStringList("events.kingdom_join");
        }
        return new ArrayList<>();
    }

    public List<String> getOnKingdomLeaveCommands() {
        if ( config.contains("events.kingdom-leave") ) {
            return config.getStringList("events.kingdom-leave");
        } else if ( config.contains("events.kingdom_leave") ) {
            return config.getStringList("events.kingdom_leave");
        }
        return new ArrayList<>();
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

    public String getOnKillWeaponMessage() {
        return config.contains("messages.kill-weapon") ? config.getString("messages.kill-weapon") : null;
    }

    public String getNoKingdomPrefix() {
        return config.contains("nokingdom.prefix") ? config.getString("nokingdom.prefix") : "";
    }

    public String getNoKingdomSuffix() {
        return config.contains("nokingdom.suffix") ? config.getString("nokingdom.suffix") : "";
    }

    public String getNoKingdomDisplay() {
        return config.contains("nokingdom.display") ? config.getString("nokingdom.display") : "";
    }

    public boolean isChatEnabledInDisabledWorlds() {
        return config.contains("enable-chat-in-disabled-worlds") && config.getBoolean("enable-chat-in-disabled-worlds");
    }

    public boolean showJoinAndLeaveKingdomOnly() {
        return config.contains("join-and-leave-kingdom-only") && config.getBoolean("join-and-leave-kingdom-only");
    }
}
