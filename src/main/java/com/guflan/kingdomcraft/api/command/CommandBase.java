package com.guflan.kingdomcraft.api.command;

import com.guflan.kingdomcraft.api.entity.CommandSender;

import java.util.List;

public interface CommandBase {

    public List<String> getCommands();

    public boolean isPlayerOnly();
    public int getExpectedArguments();

    public void execute(CommandSender sender, String[] args);

    public default List<String> autocomplete(CommandSender sender, String[] args) {
        return null;
    }

}