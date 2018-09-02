package com.igufguf.kingdomcraft.api.exceptions;

import org.bukkit.plugin.Plugin;

/**
 * Created by Joris on 17/08/2018 in project KingdomCraft.
 */
public class PluginNotEnabledException extends RuntimeException {

    public PluginNotEnabledException(Plugin plugin) {
        super("You cannot do this action while " + plugin.getName() + " (v" + plugin.getDescription().getVersion() + ") is not enabled yet!");
    }

}
