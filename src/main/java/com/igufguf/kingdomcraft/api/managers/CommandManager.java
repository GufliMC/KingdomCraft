package com.igufguf.kingdomcraft.api.managers;

import com.igufguf.kingdomcraft.api.commands.CommandBase;
import com.igufguf.kingdomcraft.api.commands.CommandSender;

import java.util.List;

public interface CommandManager {

    void registerCommand(CommandBase command);

    void unregisterCommand(CommandBase command);

    void execute(CommandSender sender, String[] args);

    List<String> autocomplete(CommandSender sender, String[] args);

}
