package com.igufguf.kingdomcraft.common.commands;

import com.igufguf.kingdomcraft.api.KingdomCraftPlugin;
import com.igufguf.kingdomcraft.api.commands.CommandBase;
import com.igufguf.kingdomcraft.api.commands.CommandSender;

import java.util.ArrayList;
import java.util.List;

public abstract class DefaultCommandBase implements CommandBase {

    protected final KingdomCraftPlugin plugin;

    private final List<String> commands = new ArrayList<>();

    private boolean isPlayerOnly;
    private int expectedArguments = 0;

    public DefaultCommandBase(KingdomCraftPlugin plugin, String command) {
        this.plugin = plugin;
        this.commands.add(command);
    }

    public DefaultCommandBase(KingdomCraftPlugin plugin, String command, int expectedArguments) {
        this(plugin, command);
        this.expectedArguments = expectedArguments;
    }

    public DefaultCommandBase(KingdomCraftPlugin plugin, String command, int expectedArguments, boolean isPlayerOnly) {
        this(plugin, command, expectedArguments);
        this.isPlayerOnly = isPlayerOnly;
    }


    protected void addCommand(String alias) {
        commands.add(alias);
    }

    @Override
    public boolean isPlayerOnly() {
        return isPlayerOnly;
    }

    @Override
    public int getExpectedArguments() {
        return expectedArguments;
    }

    @Override
    public List<String> getCommands() {
        return commands;
    }

    @Override
    public abstract void execute(CommandSender sender, String[] args);
}
