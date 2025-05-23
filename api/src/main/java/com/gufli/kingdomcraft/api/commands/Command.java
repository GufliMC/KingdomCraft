package com.gufli.kingdomcraft.api.commands;

import com.gufli.kingdomcraft.api.entity.PlatformPlayer;
import com.gufli.kingdomcraft.api.entity.PlatformSender;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public abstract class Command {

    private final List<String> commands = new ArrayList<>();

    private boolean isPlayerOnly;
    private int expectedArguments = -1;

    private String argumentsHint;

    private Supplier<String> explanationMessage;

    private String[] permissions;

    public Command(String command) {
        this.commands.add(command);
    }

    public Command(String command, int expectedArguments) {
        this(command);
        this.expectedArguments = expectedArguments;
    }

    public Command(String command, int expectedArguments, boolean isPlayerOnly) {
        this(command, expectedArguments);
        this.isPlayerOnly = isPlayerOnly;
    }

    protected void addCommand(String alias) {
        commands.add(alias);
    }

    public boolean isPlayerOnly() {
        return isPlayerOnly;
    }

    public int getExpectedArguments() {
        return expectedArguments;
    }

    public String getArgumentsHint() {
        return argumentsHint;
    }

    protected void setArgumentsHint(String argumentsHint) {
        this.argumentsHint = argumentsHint;
    }

    public String getExplanationMessage() {
        if ( hasExplanationMessage() ) {
            return explanationMessage.get();
        }
        return null;
    }

    public boolean  hasExplanationMessage() {
        return explanationMessage != null;
    }

    protected void setExplanationMessage(Supplier<String> supplier) {
        this.explanationMessage = supplier;
    }

    public String[] getPermissions() {
        return permissions;
    }

    public void setPermissions(String... permissions) {
        this.permissions = permissions;
    }

    public List<String> getCommands() {
        return commands;
    }

    public String getMainCommand() {
        return commands.get(0);
    }

    public abstract void execute(PlatformSender sender, String[] args);

    public List<String> autocomplete(PlatformPlayer player, String[] args) {
        return null;
    }

}
