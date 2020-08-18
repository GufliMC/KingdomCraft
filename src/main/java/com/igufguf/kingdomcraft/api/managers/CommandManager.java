package com.igufguf.kingdomcraft.api.managers;

import com.igufguf.kingdomcraft.api.command.CommandBase;
import com.igufguf.kingdomcraft.api.command.CommandSender;

import java.util.List;

public interface CommandManager {

    void registerCommand(CommandBase command);

    void unregisterCommand(CommandBase command);

    void execute(CommandSender sender, String[] args);

    List<String> autocomplete(CommandSender sender, String[] args);

}
