package com.guflan.kingdomcraft.common.command;

import com.guflan.kingdomcraft.api.KingdomCraft;
import com.guflan.kingdomcraft.api.command.CommandBase;
import com.guflan.kingdomcraft.api.entity.CommandSender;

import java.util.ArrayList;
import java.util.List;

public abstract class DefaultCommandBase implements CommandBase {

    protected final KingdomCraft kdc;

    private final List<String> commands = new ArrayList<>();

    private boolean isPlayerOnly;
    private int expectedArguments = 0;

    public DefaultCommandBase(KingdomCraft kdc, String command) {
        this.kdc = kdc;
        this.commands.add(command);
    }

    public DefaultCommandBase(KingdomCraft kdc, String command, int expectedArguments) {
        this(kdc, command);
        this.expectedArguments = expectedArguments;
    }

    public DefaultCommandBase(KingdomCraft kdc, String command, int expectedArguments, boolean isPlayerOnly) {
        this(kdc, command, expectedArguments);
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
