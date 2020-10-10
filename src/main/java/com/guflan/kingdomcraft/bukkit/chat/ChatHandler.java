package com.guflan.kingdomcraft.bukkit.chat;

import com.guflan.kingdomcraft.api.KingdomCraft;
import com.guflan.kingdomcraft.api.chat.ChatChannel;
import com.guflan.kingdomcraft.api.chat.ChatManager;
import com.guflan.kingdomcraft.common.chat.DefaultChatChannel;
import com.guflan.kingdomcraft.common.chat.KingdomChatChannel;
import com.guflan.kingdomcraft.api.domain.Kingdom;
import com.guflan.kingdomcraft.api.event.EventListener;
import com.guflan.kingdomcraft.bukkit.BukkitKingdomCraftPlugin;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.logging.Level;

public class ChatHandler implements EventListener {

    private final KingdomCraft kdc;

    private ConfigurationSection kingdomChannelConfig;

    public ChatHandler(Plugin plugin, KingdomCraft kdc) {
        this.kdc = kdc;

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

        ChatManager cm = kdc.getChatManager();

        if ( config.contains("kingdom-channels") && config.getBoolean("kingdom-channels")) {
            kingdomChannelConfig = config.getConfigurationSection("kingdom-channel");
            for ( Kingdom kd : kdc.getKingdoms() ) {
                kingdomChannelConfig.set("kingdom", kd.getName());
                ChatChannel ch = parse(kd.getName(), kingdomChannelConfig);
                if ( ch != null ) {
                    cm.addChatChannel(ch);
                }
            }
        }

        if ( !config.contains("channels") || config.getConfigurationSection("channels") == null ) {
            return;
        }

        ConfigurationSection channels = config.getConfigurationSection("channels");
        for ( String name : channels.getKeys(false) ) {
            ConfigurationSection cs = channels.getConfigurationSection(name);

            if ( cs.contains("kingdom") && cs.getString("kingdom").equals("*") ) {
                for ( Kingdom kd : kdc.getKingdoms() ) {
                    cs.set("kingdom", kd.getName());
                    ChatChannel ch = parse(kd.getName(), cs);
                    if ( ch != null ) {
                        cm.addChatChannel(ch);
                    }
                }
                continue;
            }

            ChatChannel ch = parse(name, cs);
            if ( ch != null ) {
                cm.addChatChannel(ch);
            }
        }
    }

    private ChatChannel parse(String name, ConfigurationSection section) {

        ChatChannel channel;
        if ( section.contains("kingdom") ) {
            Kingdom kd = kdc.getKingdom(section.getString("kingdom"));
            if ( kd == null ) {
                kdc.getPlugin().log("Cannot create channel with name '" + name + "' because the given kingdom doesn't exist.", Level.WARNING);
                return null;
            }

            channel = new KingdomChatChannel(name, kd);
        } else {
            channel = new DefaultChatChannel(name);
        }

        if ( !section.contains("format") ) {
            kdc.getPlugin().log("Cannot create channel with name '" + name + "' because no format is given.", Level.WARNING);
            return null;
        }
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

        return channel;
    }

    @Override
    public void onKingdomCreate(Kingdom kingdom) {
        kingdomChannelConfig.set("kingdom", kingdom.getName());
        ChatChannel ch = parse(kingdom.getName(), kingdomChannelConfig);
        if ( ch != null ) {
            kdc.getChatManager().addChatChannel(ch);
        }
    }

}
