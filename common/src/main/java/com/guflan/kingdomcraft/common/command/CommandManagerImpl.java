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
import com.guflan.kingdomcraft.common.KingdomCraftImpl;
import com.guflan.kingdomcraft.common.commands.*;
import com.guflan.kingdomcraft.common.commands.admin.*;
import com.guflan.kingdomcraft.common.commands.management.groups.*;
import com.guflan.kingdomcraft.common.commands.management.kingdom.*;
import com.guflan.kingdomcraft.common.commands.management.ranks.*;
import com.guflan.kingdomcraft.common.commands.member.*;

import java.util.ArrayList;
import java.util.List;

public class CommandManagerImpl implements CommandManager {

    final KingdomCraftImpl kdc;
    final List<CommandBase> commands = new ArrayList<>();

    public CommandManagerImpl(KingdomCraftImpl kdc) {
        this.kdc = kdc;
        registerDefaults();
    }

    @Override
    public void registerCommand(CommandBase command) {
        if ( commands.contains(command) ) {
            return;
        }
        commands.add(command);
    }

    @Override
    public void unregisterCommand(CommandBase command) {
        commands.remove(command);
    }

    @Override
    public List<CommandBase> getCommands() {
        return new ArrayList<>(commands);
    }

    public void registerDefaults() {
        commands.clear();

        // all
        registerCommand(new HelpCommand(kdc));
        registerCommand(new ListCommand(kdc));

        registerCommand(new JoinCommand(kdc));
        registerCommand(new ChatChannelCommand(kdc));

        // member
        registerCommand(new LeaveCommand(kdc));
        registerCommand(new SpawnCommand(kdc));
        registerCommand(new InviteCommand(kdc));

        registerCommand(new SetSpawnCommand(kdc));

        registerCommand(new AllyCommand(kdc));
        registerCommand(new EnemyCommand(kdc));
        registerCommand(new NeutralCommand(kdc));
        registerCommand(new TruceCommand(kdc));

        // admin
        registerCommand(new SetRankCommand(kdc));
        registerCommand(new SetKingdomCommand(kdc));
        registerCommand(new KickCommand(kdc));

        registerCommand(new SpawnOtherCommand(kdc));
        registerCommand(new SetSpawnOtherCommand(kdc));

        registerCommand(new TpHereCommand(kdc));
        registerCommand(new TpHereOtherCommand(kdc));

        registerCommand(new ChatCommand(kdc));

        /* Management */

        // kingdoms
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

        // ranks
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

        // groups
        registerCommand(new GroupsListCommand(kdc));
        registerCommand(new GroupsListOtherCommand(kdc));

        registerCommand(new GroupsAddCommand(kdc));
        registerCommand(new GroupsAddOtherCommand(kdc));

        registerCommand(new GroupsRemoveCommand(kdc));
        registerCommand(new GroupsRemoveOtherCommand(kdc));

        // TODO
    }

}
