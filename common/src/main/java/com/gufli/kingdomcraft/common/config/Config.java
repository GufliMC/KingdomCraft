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

import com.gufli.kingdomcraft.api.config.KingdomCraftConfig;
import com.gufli.kingdomcraft.api.domain.RelationType;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Config implements KingdomCraftConfig {

    private List<String> commandAliases = Arrays.asList("k", "kd", "kdc", "kingdom");

    private int teleportDelay = 0;
    private String language = "en";
    private List<String> worlds = new ArrayList<>();
    private ZoneId timezone = ZoneId.systemDefault();
    private DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");;
    private DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");;
    private DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");;
    private List<RelationType> friendlyFireRelations = new ArrayList<>();
    private boolean friendlyFire = false;
    private List<String> kingdomJoinCommands = new ArrayList<>();
    private List<String> kingdomLeaveCommands = new ArrayList<>();
    private boolean respawnAtKingdom = true;

    private String joinMessage;
    private String leaveMessage;
    private String deathMessage;
    private String killMessage;
    private String killWeaponMessage;

    private String noKingdomPrefix;
    private String noKingdomSuffix;
    private String noKingdomDisplay;

    private boolean isChatEnabledInDisabledWorlds = false;
    private boolean showJoinAndLeaveMessagesKingdomOnly = false;

    private boolean updateDisplayNames = false;

    public void load(Configuration config) {
        if ( config.contains("command-aliases") ) {
            commandAliases = config.getStringList("command-aliases");
        }

        if ( config.contains("teleport-delay") ) {
            teleportDelay = config.getInt("teleport-delay");
        }

        if ( config.contains("worlds") ) {
            worlds = config.getStringList("worlds");
        }

        if ( config.contains("language") ) {
            language = config.getString("language");
        }

        if ( config.contains("timezone") ) {
            try {
                timezone = ZoneId.of(config.getString("timezone"));
            } catch (Exception ignored) {}
        }

        if ( config.contains("date-format") ) {
            try {
                dateFormat = DateTimeFormatter.ofPattern(config.getString("date-format"));
            } catch (Exception ignored) {}
        }

        if ( config.contains("time-format") ) {
            try {
                timeFormat = DateTimeFormatter.ofPattern(config.getString("time-format"));
            } catch (Exception ignored) {}
        }

        String dateFormat = config.contains("date-format") ? config.getString("date-format") : "yyyy-MM-dd";
        String timeFormat = config.contains("time-format") ? config.getString("time-format") : "HH:mm";
        try {
            dateTimeFormat = DateTimeFormatter.ofPattern(dateFormat + " " + timeFormat);
        } catch (Exception ignored) {}

        if ( config.contains("friendly-fire-relationships") ) {
            friendlyFireRelations = config.getStringList("friendly-fire-relationships").stream()
                    .map(s -> RelationType.valueOf(s.toUpperCase()))
                    .collect(Collectors.toList());
        }

        if ( config.contains("friendly-fire") ) {
            friendlyFire = config.getBoolean("friendly-fire");
        }

        if ( config.contains("events.kingdom-join") ) {
            kingdomJoinCommands = config.getStringList("events.kingdom-join");
        }
        // TODO remove old key
        else if ( config.contains("events.kingdom_join") ) {
            kingdomJoinCommands = config.getStringList("events.kingdom_join");
        }

        if ( config.contains("events.kingdom-leave") ) {
            kingdomLeaveCommands = config.getStringList("events.kingdom-leave");
        }
        // TODO remove old key
        else if ( config.contains("events.kingdom_leave") ) {
            kingdomLeaveCommands = config.getStringList("events.kingdom_leave");
        }

        if ( config.contains("respawn-at-kingdom") ) {
            respawnAtKingdom = config.getBoolean("respawn-at-kingdom");
        }

        if ( config.contains("messages.join") ) {
            joinMessage = config.getString("messages.join");
        }

        if ( config.contains("messages.leave") ) {
            leaveMessage = config.getString("messages.leave");
        }

        if ( config.contains("messages.death") ) {
            deathMessage = config.getString("messages.death");
        }

        if ( config.contains("messages.kill") ) {
            killMessage = config.getString("messages.kill");
        }

        if ( config.contains("messages.kill-weapon") ) {
            killWeaponMessage = config.getString("messages.kill-weapon");
        }

        if ( config.contains("nokingdom.prefix") ) {
            noKingdomPrefix = config.getString("nokingdom.prefix");
        }

        if ( config.contains("nokingdom.suffix") ) {
            noKingdomSuffix = config.getString("nokingdom.suffix");
        }

        if ( config.contains("nokingdom.display") ) {
            noKingdomDisplay = config.getString("nokingdom.display");
        }

        if ( config.contains("enable-chat-in-disabled-worlds") ) {
            isChatEnabledInDisabledWorlds = config.getBoolean("enable-chat-in-disabled-worlds");
        }

        if ( config.contains("join-and-leave-kingdom-only") ) {
            showJoinAndLeaveMessagesKingdomOnly = config.getBoolean("join-and-leave-kingdom-only");
        }

        if ( config.contains("update-display-names") ) {
            updateDisplayNames = config.getBoolean("update-display-names");
        }
    }

    @Override
    public List<String> getCommandAliases() {
        return commandAliases;
    }

    @Override
    public int getTeleportDelay() {
        return teleportDelay;
    }

    @Override
    public List<String> getWorlds() {
        return worlds;
    }

    @Override
    public String getLanguage() {
        return language;
    }

    @Override
    public ZoneId getTimeZone() {
        return timezone;
    }

    @Override
    public DateTimeFormatter getDateFormat() {
        return dateFormat;
    }

    @Override
    public DateTimeFormatter getTimeFormat() {
        return timeFormat;
    }

    @Override
    public DateTimeFormatter getDateTimeFormat() {
        return dateTimeFormat;
    }

    @Override
    public boolean isWorldEnabled(String world) {
        List<String> worlds = getWorlds();
        return worlds.isEmpty() || worlds.stream().anyMatch(s -> s.equalsIgnoreCase(world));
    }

    @Override
    public List<RelationType> getFriendlyFireRelationTypes() {
        return friendlyFireRelations;
    }

    @Override
    public boolean isFriendlyFireEnabled() {
        return friendlyFire;
    }

    @Override
    public List<String> getOnKingdomJoinCommands() {
        return kingdomJoinCommands;
    }

    @Override
    public List<String> getOnKingdomLeaveCommands() {
        return kingdomLeaveCommands;
    }

    @Override
    public boolean respawnAtKingdom() {
        return respawnAtKingdom;
    }

    @Override
    public String getOnJoinMessage() {
        return joinMessage;
    }

    @Override
    public String getOnLeaveMessage() {
        return leaveMessage;
    }

    @Override
    public String getOnDeathMessage() {
        return deathMessage;
    }

    @Override
    public String getOnKillMessage() {
        return killMessage;
    }

    @Override
    public String getOnKillWeaponMessage() {
        return killWeaponMessage;
    }

    @Override
    public String getNoKingdomPrefix() {
        return noKingdomPrefix;
    }

    @Override
    public String getNoKingdomSuffix() {
        return noKingdomSuffix;
    }

    @Override
    public String getNoKingdomDisplay() {
        return noKingdomDisplay;
    }

    @Override
    public boolean isChatEnabledInDisabledWorlds() {
        return isChatEnabledInDisabledWorlds;
    }

    @Override
    public boolean showJoinAndLeaveKingdomOnly() {
        return showJoinAndLeaveMessagesKingdomOnly;
    }

    @Override
    public boolean updateDisplayNames() {
        return updateDisplayNames;
    }
}
