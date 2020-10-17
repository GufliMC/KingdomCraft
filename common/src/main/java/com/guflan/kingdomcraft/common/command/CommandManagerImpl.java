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

package com.guflan.kingdomcraft.common.command;

import com.guflan.kingdomcraft.api.command.CommandBase;
import com.guflan.kingdomcraft.api.command.CommandManager;
import com.guflan.kingdomcraft.common.AbstractKingdomCraft;
import com.guflan.kingdomcraft.common.commands.InfoCommand;
import com.guflan.kingdomcraft.common.commands.JoinCommand;
import com.guflan.kingdomcraft.common.commands.ListCommand;
import com.guflan.kingdomcraft.common.commands.admin.KickCommand;
import com.guflan.kingdomcraft.common.commands.admin.SetKingdomCommand;
import com.guflan.kingdomcraft.common.commands.management.kingdom.*;
import com.guflan.kingdomcraft.common.commands.management.ranks.*;
import com.guflan.kingdomcraft.common.commands.member.*;

import java.util.ArrayList;
import java.util.List;

public class CommandManagerImpl implements CommandManager {

    final List<CommandBase> commands = new ArrayList<>();

    public CommandManagerImpl(AbstractKingdomCraft kdc) {
        registerDefaults(kdc);
    }

    @Override
    public void registerCommand(CommandBase command) {
        commands.add(command);
    }

    @Override
    public void unregisterCommand(CommandBase command) {
        commands.remove(command);
    }

    public void registerDefaults(AbstractKingdomCraft kdc) {
        commands.clear();

        // all
        registerCommand(new ListCommand(kdc));
        registerCommand(new JoinCommand(kdc));
        registerCommand(new InfoCommand(kdc));

        // member
        registerCommand(new LeaveCommand(kdc));
        registerCommand(new InviteCommand(kdc));

        registerCommand(new AllyCommand(kdc));
        registerCommand(new EnemyCommand(kdc));
        registerCommand(new NeutralCommand(kdc));
        registerCommand(new TruceCommand(kdc));

        // management
        registerCommand(new CreateCommand(kdc));
        registerCommand(new DeleteCommand(kdc));

        registerCommand(new EditDisplayCommand(kdc));
        registerCommand(new EditDisplayOtherCommand(kdc));

        registerCommand(new EditPrefixCommand(kdc));
        registerCommand(new EditPrefixOtherCommand(kdc));

        registerCommand(new EditSuffixCommand(kdc));
        registerCommand(new EditSuffixOtherCommand(kdc));

        registerCommand(new EditInviteOnlyCommand(kdc));
        registerCommand(new EditInviteOnlyOtherCommand(kdc));

        registerCommand(new EditMaxMembersCommand(kdc));
        registerCommand(new EditMaxMembersOtherCommand(kdc));

        registerCommand(new RanksListCommand(kdc));
        registerCommand(new RanksListOtherCommand(kdc));

        registerCommand(new RanksCreateCommand(kdc));
        registerCommand(new RanksCreateOtherCommand(kdc));

        registerCommand(new RanksDeleteCommand(kdc));
        registerCommand(new RanksDeleteOtherCommand(kdc));

        registerCommand(new RanksEditDisplayCommand(kdc));
        registerCommand(new RanksEditDisplayOtherCommand(kdc));

        registerCommand(new RanksEditPrefixCommand(kdc));
        registerCommand(new RanksEditPrefixOtherCommand(kdc));

        registerCommand(new RanksEditSuffixCommand(kdc));
        registerCommand(new RanksEditSuffixOtherCommand(kdc));

        registerCommand(new RanksEditMaxMembersCommand(kdc));
        registerCommand(new RanksEditMaxMembersOtherCommand(kdc));

        registerCommand(new RanksEditLevelCommand(kdc));
        registerCommand(new RanksEditLevelOtherCommand(kdc));

        // admin
        registerCommand(new KickCommand(kdc));
        registerCommand(new SetKingdomCommand(kdc));


        // TODO
    }

}
