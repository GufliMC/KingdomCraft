package com.igufguf.kingdomcraft.common.commands;

import com.igufguf.kingdomcraft.api.models.CommandBase;
import com.igufguf.kingdomcraft.api.models.CommandSender;
import com.igufguf.kingdomcraft.api.models.PlayerCommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public abstract class DefaultCommandBase implements CommandBase<CommandSender> {

    private final List<String> commands = new ArrayList<>();
    private final String[] arguments;

    private boolean playerOnly = false;

    public DefaultCommandBase(String command, String arguments) {
        commands.add(command);
        this.arguments = arguments.split(Pattern.quote(" "));
    }

    public DefaultCommandBase(String command, String arguments, boolean playerOnly) {
        this(command, arguments);
        this.playerOnly = playerOnly;
    }

    protected void addAlias(String alias) {
        commands.add(alias);
    }

    @Override
    public boolean preflight(CommandSender sender) {
        if ( playerOnly && !(sender instanceof PlayerCommandSender) ) {
            // TODO message
            return false;
        }
        return true;
    }

    @Override
    public List<String> getCommands() {
        return commands;
    }

    @Override
    public String[] getArguments() {
        return arguments;
    }

    @Override
    public abstract void execute(CommandSender player, String[] args);
}
