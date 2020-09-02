package com.igufguf.kingdomcraft.bukkit.chat;

import com.igufguf.kingdomcraft.api.KingdomCraftPlugin;
import com.igufguf.kingdomcraft.api.chat.ChatChannel;
import com.igufguf.kingdomcraft.api.chat.ChatManager;
import com.igufguf.kingdomcraft.api.domain.Kingdom;
import com.igufguf.kingdomcraft.api.event.EventListener;
import com.igufguf.kingdomcraft.bukkit.KingdomCraft;
import com.igufguf.kingdomcraft.common.chat.DefaultChatChannel;
import com.igufguf.kingdomcraft.common.chat.KingdomChatChannel;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class BukkitChat implements EventListener {

    private final KingdomCraftPlugin plugin;

    private ConfigurationSection kingdomChannelConfig;

    public BukkitChat(KingdomCraft plugin) {
        this.plugin = plugin;
        plugin.getEventManager().addListener(this);

        File configFile = new File(plugin.getDataFolder(), "chat.yml");
        if ( !configFile.exists() ) {
            plugin.saveResource("chat.yml", false);
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        if ( !config.contains("enabled") || !config.getBoolean("enabled") ) {
            return;
        }

        Bukkit.getPluginManager().registerEvents(new ChatListener(plugin), plugin);

        ChatManager cm = plugin.getChatManager();

        if ( config.contains("kingdom-channels") && config.getBoolean("kingdom-channels")) {
            kingdomChannelConfig = config.getConfigurationSection("kingdom-channel");
            for ( Kingdom kd : plugin.getKingdomManager().getKingdoms() ) {
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
                for ( Kingdom kd : plugin.getKingdomManager().getKingdoms() ) {
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
            Kingdom kd = plugin.getKingdomManager().getKingdom(section.getString("kingdom"));
            if ( kd == null ) {
                System.out.println("Cannot create channel with name '" + name + "' because the given kingdom doesn't exist.");
                return null;
            }

            channel = new KingdomChatChannel(name, kd);
        } else {
            channel = new DefaultChatChannel(name);
        }

        if ( !section.contains("format") ) {
            System.out.println("Cannot create channel with name '" + name + "' because no format is given.");
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
            plugin.getChatManager().addChatChannel(ch);
        }
    }

}
