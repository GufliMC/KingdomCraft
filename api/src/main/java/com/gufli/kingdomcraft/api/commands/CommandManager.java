package com.gufli.kingdomcraft.api.commands;

import java.util.List;

public interface CommandManager {

    void addCommand(Command command);

    void removeCommand(Command command);

    List<Command> getCommands();

}
