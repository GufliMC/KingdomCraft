/*
 * This file is part of KingdomCraft.
 *
 * KingdomCraft is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * KingdomCraft is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with KingdomCraft. If not, see <https://www.gnu.org/licenses/>.
 */

package com.gufli.kingdomcraft.common.command;

import com.gufli.kingdomcraft.api.commands.Command;
import com.gufli.kingdomcraft.api.entity.PlatformPlayer;
import com.gufli.kingdomcraft.api.entity.PlatformSender;
import com.gufli.kingdomcraft.common.KingdomCraftImpl;

import java.util.ArrayList;
import java.util.List;

public abstract class CommandBase extends Command {

    protected final KingdomCraftImpl kdc;

    public CommandBase(KingdomCraftImpl kdc, String command) {
        super(command);
        this.kdc = kdc;
    }

    public CommandBase(KingdomCraftImpl kdc, String command, int expectedArguments) {
        super(command, expectedArguments);
        this.kdc = kdc;
    }

    public CommandBase(KingdomCraftImpl kdc, String command, int expectedArguments, boolean isPlayerOnly) {
        super(command, expectedArguments, isPlayerOnly);
        this.kdc = kdc;
    }

}
