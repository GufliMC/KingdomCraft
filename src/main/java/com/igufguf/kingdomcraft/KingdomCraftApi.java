package com.igufguf.kingdomcraft;

import com.igufguf.kingdomcraft.managers.*;
import org.bukkit.World;

import java.io.File;
import java.io.IOException;
import java.util.*;

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
 * along with KingdomCraft. If not, see <http://www.gnu.org/licenses/>.
 *
 **/
public class KingdomCraftApi {

    private final KingdomCraft plugin;

    private FlagManager flagManager;

    private KingdomManager kingdomManager;
    private UserManager userManager;
    private RelationManager relationManager;
    private PermissionManager permissionManager;
    private ChatManager chatManager;
    private TeleportManager teleportManager;

    protected KingdomCraftApi(KingdomCraft plugin) {
        this.plugin = plugin;

        File datadir = new File(plugin.getDataFolder(), "/data/");
        if ( !datadir.exists() ) {
            datadir.mkdirs();
        }

        flagManager = new FlagManager(this);
    }

    protected void initialize() {
        try {
            kingdomManager = new KingdomManager(this);
            userManager = new UserManager(this);
            relationManager = new RelationManager(this);
            permissionManager = new PermissionManager(this);
            chatManager = new ChatManager(this);
            teleportManager = new TeleportManager(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public KingdomCraft getPlugin() {
        return plugin;
    }

    public KingdomManager getKingdomManager() {
        return kingdomManager;
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public RelationManager getRelationManager() {
        return relationManager;
    }

    public PermissionManager getPermissionManager() {
        return permissionManager;
    }

    public ChatManager getChatManager() {
        return chatManager;
    }

    public TeleportManager getTeleportManager() {
        return teleportManager;
    }

    public FlagManager getFlagManager() {
        return flagManager;
    }

    public boolean isWorldEnabled(World world) {
        if ( !plugin.getCfg().getBoolean("worlds") ) return true;
        List<String> worlds = plugin.getCfg().getStringList("world-list");

        for ( String w : worlds ) {
            if ( w.equalsIgnoreCase(world.getName()) ) return true;
        }
        return false;
    }

}
