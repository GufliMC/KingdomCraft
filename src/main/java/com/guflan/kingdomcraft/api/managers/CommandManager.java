package com.guflan.kingdomcraft.api.managers;

import com.guflan.kingdomcraft.api.command.CommandBase;
import com.guflan.kingdomcraft.api.entity.CommandSender;

import java.util.List;

public interface CommandManager {

    void registerCommand(CommandBase command);

    void unregisterCommand(CommandBase command);

    void execute(CommandSender sender, String[] args);

    List<String> autocomplete(CommandSender sender, String[] args);

}
