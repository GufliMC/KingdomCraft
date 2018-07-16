package com.igufguf.kingdomcraft.managers;

import com.igufguf.kingdomcraft.KingdomCraft;
import com.igufguf.kingdomcraft.KingdomCraftApi;
import com.igufguf.kingdomcraft.KingdomCraftConfig;
import com.igufguf.kingdomcraft.listeners.ChatListener;
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

    private boolean chatsystem = false;

    private boolean antiadvertise = false;
    private boolean anticaps = false;

    private boolean channelsenabled = false;

    private String nokingdomchannel;
    private String defaultchannel;
    private ArrayList<Channel> channels;

    private String defaultformat;

    public ChatManager(KingdomCraftApi api) {
        KingdomCraft plugin = api.getPlugin();

        if ( plugin.getServer().getPluginManager().getPlugin("Vault") != null ) {
            RegisteredServiceProvider<Chat> chatProvider = plugin.getServer().getServicesManager().getRegistration(Chat.class);
            if (chatProvider != null) {
                vault = chatProvider.getProvider();
            }
        }

        KingdomCraftConfig config = plugin.getCfg();

        chatsystem = config.getBoolean("chat-system");
        if ( !chatsystem ) return;

        antiadvertise = config.getBoolean("anti-advertise");
        anticaps = config.getBoolean("anti-caps");

        channelsenabled = config.getBoolean("channels.enabled");
        if ( !channelsenabled ) {

            if ( config.has("nochannels-format") ) {
                defaultformat = config.getString("nochannels-format");
            }

            return;
        }

        channels = new ArrayList<>();

        // load channels
        for ( String name : config.getSection("channels").getKeys(false) ) {
            if ( name.equalsIgnoreCase("enabled") ) continue;
            if ( !config.has("channels." + name + ".format") ) continue;

            if ( config.getBoolean("channels." + name + ".default") ) {
                defaultchannel = name;
            }

            VisibilityType type = VisibilityType.PUBLIC;

            if ( config.has("channels." + name + ".visibility") ) {

                String s = config.getString("channels." + name + ".visibility");
                if ( s.equalsIgnoreCase("kingdom") ) type = VisibilityType.KINGDOM;
                else if ( s.equalsIgnoreCase("public") ) type = VisibilityType.PUBLIC;

            } else if ( config.getBoolean("channels." + name + ".kingdom-only") ) {
                type = VisibilityType.KINGDOM;
            }

            String format = config.getString("channels." + name + ".format");

            String mprefix = config.getString("channels." + name + ".message-prefix");

            boolean alwayson = config.has("channels." + name + ".alwayson") && config.getBoolean("channels." + name + ".alwayson");
            boolean permission = config.has("channels." + name + ".permission") && config.getBoolean("channels." + name + ".permission");

            Channel c = new Channel(name, format, mprefix, type, alwayson, permission);
            addChannel(c);
        }

        if ( config.has("nokingdom-channel") ) {
            nokingdomchannel = config.getString("nokingdom-channel");
        }

        if ( config.has("defaultformat") ) {
            defaultformat = config.getString("defaultformat");
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

    public String getDefaultChannel() {
        return defaultchannel;
    }

    public String getNoKingdomChannel() {
        return nokingdomchannel;
    }

    public class Channel {

        private final String name;

        private final String format;
        private final String mprefix;

        private final VisibilityType vtype;
        private final boolean alwayson;
        private final boolean permission;

        public Channel(String name, String format, String mprefix, VisibilityType vtype, boolean alwayson, boolean permission) {
            this.name = name;
            this.format = StringEscapeUtils.unescapeJava(format);
            this.mprefix = mprefix;
            this.vtype = vtype;
            this.alwayson = alwayson;
            this.permission = permission;
        }

        public Channel(String name, String format, String mprefix) {
            this(name, format, mprefix, VisibilityType.PUBLIC, false, false);
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

        public boolean isAlwayson() {
            return alwayson;
        }

        public boolean isPermission() {
            return permission;
        }

    }

    public enum VisibilityType {
        PUBLIC, KINGDOM;
    }
}
