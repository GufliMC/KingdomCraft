package com.igufguf.kingdomcraft.managers;

import com.igufguf.kingdomcraft.KingdomCraft;
import com.igufguf.kingdomcraft.KingdomCraftConfig;
import com.igufguf.kingdomcraft.api.models.kingdom.KingdomUser;
import net.milkbowl.vault.chat.Chat;
import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyrighted 2018 iGufGuf
 *
 * This file is part of KingdomCraft.
 *
 * Kingdomcraft is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * KingdomCraft is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with KingdomCraft.  If not, see <http://www.gnu.org/licenses/>.
 *
 **/
public class ChatManager {

    private Chat vault;

    private boolean chatsystem;

    private boolean antiadvertise = false;
    private boolean anticaps = false;

    private boolean channelsenabled = false;

    private String kingdomlessDefaultChannel;
    private String kingdomDefaultChannel;
    private ArrayList<Channel> channels;

    private String defaultformat;

    public ChatManager(KingdomCraft plugin) {
        if ( plugin.getServer().getPluginManager().getPlugin("Vault") != null ) {
            RegisteredServiceProvider<Chat> chatProvider = plugin.getServer().getServicesManager().getRegistration(Chat.class);
            if (chatProvider != null) {
                vault = chatProvider.getProvider();
            }
        }

        KingdomCraftConfig config = plugin.getCfg();

        chatsystem = config.getBoolean("chat-system.enabled");
        if ( !chatsystem ) return;

        String pathprefix = "chat-system.";

        antiadvertise = config.getBoolean(pathprefix + "anti-advertise");
        anticaps = config.getBoolean(pathprefix + "anti-caps");

        channelsenabled = config.getBoolean(pathprefix + "channels.enabled");
        if ( !channelsenabled ) {
            if ( config.has(pathprefix + "default-format") ) {
                defaultformat = config.getString(pathprefix + "default-format");
            }
            return;
        }

        channels = new ArrayList<>();

        pathprefix = pathprefix + "channels.";

        // load channels
        for ( String name : config.getSection(pathprefix.substring(0, pathprefix.length()-1)).getKeys(false) ) {

            // non channels in this path
            if ( name.equalsIgnoreCase("enabled") ) continue;

            // channel must have format
            if ( !config.has(pathprefix + name + ".format") ) continue;

            // channel visiblity
            VisibilityType type = VisibilityType.PUBLIC;
            if ( config.has(pathprefix + name + ".visibility") ) {
                String s = config.getString(pathprefix + name + ".visibility");

                if ( s.equalsIgnoreCase("kingdom") ) type = VisibilityType.KINGDOM;
                else if ( s.equalsIgnoreCase("public") ) type = VisibilityType.PUBLIC;
            }

            // is it a default channel
            boolean defaultChannel = config.getBoolean(pathprefix + name + ".default");
            if ( defaultChannel ) {
                if ( type == VisibilityType.KINGDOM) {
                    kingdomDefaultChannel = name;
                }  else {
                    kingdomlessDefaultChannel = name;
                }
            }

            // channel format
            String format = config.getString(pathprefix + name + ".format");

            // channel message prefix
            String mprefix = config.getString(pathprefix + name + ".message-prefix");

            // channel always-enabled
            boolean alwaysEnabled = config.has(pathprefix + name + ".always-enabled") && config.getBoolean(pathprefix + name + ".always-enabled");

            // channel permission
            boolean permission = config.has(pathprefix + name + ".permission") && config.getBoolean(pathprefix + name + ".permission");

            boolean defaultEnabled = config.has(pathprefix + name + ".default-enabled") &&  config.getBoolean(pathprefix + name + ".default-enabled");

            Channel c = new Channel(name, format, mprefix, type, defaultChannel || alwaysEnabled, defaultEnabled, permission);
            addChannel(c);
        }
    }

    public boolean isChatSystemEnabled() {
        return chatsystem;
    }

    public Chat getVault() {
        return vault;
    }

    public boolean hasVault() {
        return vault != null;
    }

    public boolean isAntiAdvertiseEnabled() {
        return antiadvertise;
    }

    public boolean isAntiCapsEnabled() {
        return anticaps;
    }

    public String getDefaultFormat() {
        return defaultformat;
    }

    // channels

    public boolean areChannelsEnabled() {
        return channelsenabled;
    }

    public List<Channel> getChannels() {
        return channels;
    }

    public void addChannel(Channel c) {
        if ( !channels.contains(c) ) channels.add(c);
    }

    public Channel getChannel(String name) {
        for ( Channel c : channels ) {
            if ( c.getName().equalsIgnoreCase(name) ) return c;
        }
        return null;
    }

    public String getKingdomDefaultChannel() {
        return kingdomDefaultChannel;
    }

    public String getKingdomlessDefaultChannel() {
        return kingdomlessDefaultChannel;
    }


    // users

    public String getDefaultChannel(KingdomUser user) {
        if ( user.getKingdom() == null || getKingdomDefaultChannel() == null ) {
            return getKingdomlessDefaultChannel();
        }
        return getKingdomDefaultChannel();
    }

    public class Channel {

        private final String name;

        private final String format;
        private final String mprefix;

        private final VisibilityType vtype;

        private final boolean alwaysEnabled;
        private final boolean defaultEnabled;
        private final boolean permission;

        public Channel(String name, String format, String mprefix, VisibilityType vtype, boolean alwaysEnabled, boolean defaultEnabled, boolean permission) {
            this.name = name;
            this.format = StringEscapeUtils.unescapeJava(format);
            this.mprefix = mprefix;
            this.vtype = vtype;
            this.alwaysEnabled = alwaysEnabled;
            this.defaultEnabled = defaultEnabled;
            this.permission = permission;
        }

        public Channel(String name, String format, String mprefix) {
            this(name, format, mprefix, VisibilityType.PUBLIC, false, false, false);
        }


        public String getName() {
            return name;
        }

        public String getFormat() {
            return format;
        }

        public String getMessagePrefix() {
            return mprefix;
        }

        public VisibilityType getVisibilityType() {
            return vtype;
        }

        public boolean isAlwaysEnabled() {
            return alwaysEnabled;
        }

        public boolean isDefaultEnabled() {
            return defaultEnabled;
        }

        public boolean isPermission() {
            return permission;
        }

    }

    public enum VisibilityType {
        PUBLIC, KINGDOM;
    }
}
