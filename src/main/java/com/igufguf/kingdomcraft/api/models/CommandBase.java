package com.igufguf.kingdomcraft.api.models;

import java.util.List;

public interface CommandBase<T extends CommandSender> {

    public List<String> getCommands();
    public String[] getArguments();

    public boolean preflight(CommandSender sender);

    public void execute(T player, String[] args);

    public default List<String> autocomplete(T player, String[] args) {
        return null;
    }

}
