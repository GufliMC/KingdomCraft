/*
 * This file is part of KingdomCraft.
 *
 * KingdomCraft is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * KingdomCraft is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with KingdomCraft. If not, see <https://www.gnu.org/licenses/>.
 */

package com.guflan.kingdomcraft.api.command;

import com.guflan.kingdomcraft.api.entity.PlatformSender;

import java.util.ArrayList;
import java.util.List;

public abstract class BasicCommandBase implements CommandBase {

    private final List<String> commands = new ArrayList<>();

    private boolean isPlayerOnly;
    private int expectedArguments = -1;

    private String argumentsHint;
    private String explanationMessage;

    private String[] permissions;

    public BasicCommandBase() {}

    public BasicCommandBase(String command) {
        this.commands.add(command);
    }

    public BasicCommandBase(String command, int expectedArguments) {
        this(command);
        this.expectedArguments = expectedArguments;
    }

    public BasicCommandBase(String command, int expectedArguments, boolean isPlayerOnly) {
        this(command, expectedArguments);
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
    public String getArgumentsHint() {
        return argumentsHint;
    }

    protected void setArgumentsHint(String argumentsHint) {
        this.argumentsHint = argumentsHint;
    }

    @Override
    public String getExplanationMessage() {
        return explanationMessage;
    }

    protected void setExplanationMessage(String explanationMessage) {
        this.explanationMessage = explanationMessage;
    }

    @Override
    public String[] getPermissions() {
        return permissions;
    }

    public void setPermissions(String... permissions) {
        this.permissions = permissions;
    }

    @Override
    public List<String> getCommands() {
        return commands;
    }

    @Override
    public abstract void execute(PlatformSender sender, String[] args);
}