package com.igufguf.kingdomcraft.api.handlers;

import com.igufguf.kingdomcraft.api.models.commands.CommandBase;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;

/**
 * Created by Joris on 17/08/2018 in project KingdomCraft.
 */
public interface KingdomCommandHandler extends CommandExecutor, TabCompleter {

    void register(CommandBase base);

    void unregister(CommandBase base);

    CommandBase getByCommand(String cmd);

}
