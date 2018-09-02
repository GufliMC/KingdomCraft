package com.igufguf.kingdomcraft.api;

import com.igufguf.kingdomcraft.KingdomCraft;
import com.igufguf.kingdomcraft.api.handlers.*;
import com.igufguf.kingdomcraft.handlers.*;
import org.bukkit.World;

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
 * along with KingdomCraft. If not, see <http://www.gnu.org/licenses/>.
 *
 **/
public class KingdomCraftApi {

    private final KingdomCraft plugin;

    private KingdomHandler kingdomHandler;
    private KingdomUserHandler userHandler;

    private KingdomFlagHandler flagHandler;
    private KingdomRelationHandler relationHandler;

    private KingdomCommandHandler commandHandler;

    public KingdomCraftApi(KingdomCraft plugin) {
        this.plugin = plugin;
        this.reload();
    }

    public void reload() {
        flagHandler = new SimpleFlagHandler(plugin);
        kingdomHandler = new SimpleKingdomHandler(plugin);
        userHandler = new SimpleUserHandler(plugin);
        relationHandler = new SimpleRelationHandler(plugin);
        commandHandler = new SimpleCommandHandler(plugin);
    }

    public KingdomCraft getPlugin() {
        return plugin;
    }

    public KingdomHandler getKingdomHandler() {
        return kingdomHandler;
    }

    public KingdomUserHandler getUserHandler() {
        return userHandler;
    }

    public KingdomRelationHandler getRelationHandler() {
        return relationHandler;
    }

    public KingdomFlagHandler getFlagHandler() {
        return flagHandler;
    }

    public KingdomCommandHandler getCommandHandler() {
        return commandHandler;
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
