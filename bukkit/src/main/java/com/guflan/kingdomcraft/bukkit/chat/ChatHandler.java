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

package com.guflan.kingdomcraft.bukkit.chat;

import com.guflan.kingdomcraft.api.KingdomCraft;
import com.guflan.kingdomcraft.api.chat.ChatChannel;
import com.guflan.kingdomcraft.api.chat.ChatChannelBlueprint;
import com.guflan.kingdomcraft.api.chat.ChatManager;
import com.guflan.kingdomcraft.api.domain.models.Kingdom;
import com.guflan.kingdomcraft.api.event.EventListener;
import com.guflan.kingdomcraft.common.chat.channels.BasicChatChannel;
import com.guflan.kingdomcraft.common.chat.channels.KingdomChatChannel;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ChatHandler implements EventListener {

    public ChatHandler(Plugin plugin, KingdomCraft kdc) {
        kdc.getEventManager().addListener(this);

        File configFile = new File(plugin.getDataFolder(), "chat.yml");
        if ( !configFile.exists() ) {
            plugin.saveResource("chat.yml", false);
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        if ( !config.contains("enabled") || !config.getBoolean("enabled") ) {
            return;
        }

        Bukkit.getPluginManager().registerEvents(new ChatListener(kdc), plugin);

        if ( !config.contains("channels") || config.getConfigurationSection("channels") == null ) {
            return;
        }
        ChatManager cm = kdc.getChatManager();

        ConfigurationSection channels = config.getConfigurationSection("channels");
        for ( String name : channels.getKeys(false) ) {
            ConfigurationSection cs = channels.getConfigurationSection(name);

            if ( !cs.contains("format") ) {
                kdc.getPlugin().log("Cannot create channel with name '" + name + "' because no format is given.", Level.WARNING);
                continue;
            }

            if ( cs.contains("kingdom") ) {
                ChatChannelBlueprint bp = createBlueprint(name, cs);
                cm.addBlueprint(bp);
            } else {
                ChatChannel ch = new BasicChatChannel(name);
                setup(ch, cs);
                cm.addChatChannel(ch);

                if ( cs.contains("default") && cs.getBoolean("default") ) {
                    cm.setDefaultChatChannel(ch);
                }
            }
        }
    }

    private void setup(ChatChannel channel, ConfigurationSection section) {
        channel.setFormat(section.getString("format"));

        if ( section.contains("prefix") ) {
            channel.setPrefix(section.getString("prefix"));
        }

        if ( section.contains("toggleable") ) {
            channel.setToggleable(section.getBoolean("toggleable"));
        }

        if ( section.contains("restricted") ) {
            channel.setRestricted(section.getBoolean("restricted"));
        }

        if ( section.contains("range") ) {
            channel.setRange(section.getInt("range"));
        }
    }

    private ChatChannelBlueprint createBlueprint(String name, ConfigurationSection section) {
        String target = section.getString("kingdom");
        List<String> kingdoms = Arrays.stream(target.split(Pattern.quote(",")))
                .map(String::toLowerCase)
                .collect(Collectors.toList());

        return new ChatChannelBlueprint() {
            @Override
            public boolean doesTarget(Kingdom kingdom) {
                return target.equals("*") || kingdoms.contains(kingdom.getName().toLowerCase());
            }

            @Override
            public String getName() {
                return name;
            }

            @Override
            public ChatChannel create(Kingdom kingdom) {
                ChatChannel ch = new KingdomChatChannel(getName() + "-" + kingdom.getName(), kingdom);
                setup(ch, section);
                return ch;
            }
        };
    }


}
