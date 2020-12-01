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

import com.gufli.kingdomcraft.common.KingdomCraftImpl;
import com.gufli.kingdomcraft.common.commands.admin.*;
import com.gufli.kingdomcraft.common.commands.chat.ChatChannelCommand;
import com.gufli.kingdomcraft.common.commands.chat.ChatCommand;
import com.gufli.kingdomcraft.common.commands.edit.groups.*;
import com.gufli.kingdomcraft.common.commands.edit.kingdom.*;
import com.gufli.kingdomcraft.common.commands.edit.ranks.*;
import com.gufli.kingdomcraft.common.commands.general.HelpCommand;
import com.gufli.kingdomcraft.common.commands.general.JoinCommand;
import com.gufli.kingdomcraft.common.commands.general.ListCommand;
import com.gufli.kingdomcraft.common.commands.member.InviteCommand;
import com.gufli.kingdomcraft.common.commands.member.KickCommand;
import com.gufli.kingdomcraft.common.commands.member.LeaveCommand;
import com.gufli.kingdomcraft.common.commands.member.SetRankCommand;
import com.gufli.kingdomcraft.common.commands.relations.AllyCommand;
import com.gufli.kingdomcraft.common.commands.relations.EnemyCommand;
import com.gufli.kingdomcraft.common.commands.relations.NeutralCommand;
import com.gufli.kingdomcraft.common.commands.relations.TruceCommand;
import com.gufli.kingdomcraft.common.commands.spawn.*;
import com.gufli.kingdomcraft.common.commands.tphere.TpHereCommand;
import com.gufli.kingdomcraft.common.commands.tphere.TpHereOtherCommand;

import java.util.ArrayList;
import java.util.List;

public class CommandManager {

    final KingdomCraftImpl kdc;
    final List<CommandBase> commands = new ArrayList<>();

    public CommandManager(KingdomCraftImpl kdc) {
        this.kdc = kdc;
        registerDefaults();
    }

    public void addCommand(CommandBase command) {
        if ( commands.contains(command) ) {
            return;
        }
        commands.add(command);
    }

    public void removeCommand(CommandBase command) {
        commands.remove(command);
    }

    public List<CommandBase> getCommands() {
        return new ArrayList<>(commands);
    }

    public void registerDefaults() {
        commands.clear();

        // all
        addCommand(new HelpCommand(kdc));
        addCommand(new ListCommand(kdc));

        addCommand(new JoinCommand(kdc));
        addCommand(new ChatChannelCommand(kdc));

        // member
        addCommand(new LeaveCommand(kdc));
        addCommand(new SpawnCommand(kdc));
        addCommand(new InviteCommand(kdc));

        addCommand(new SetSpawnCommand(kdc));

        addCommand(new AllyCommand(kdc));
        addCommand(new EnemyCommand(kdc));
        addCommand(new NeutralCommand(kdc));
        addCommand(new TruceCommand(kdc));

        // admin
        addCommand(new ReloadCommand(kdc));

        addCommand(new SetRankCommand(kdc));
        addCommand(new SetKingdomCommand(kdc));
        addCommand(new KickCommand(kdc));

        addCommand(new SpawnOtherCommand(kdc));
        addCommand(new SetSpawnOtherCommand(kdc));

        addCommand(new TpSpawnCommand(kdc));
        addCommand(new TpSpawnOtherCommand(kdc));

        addCommand(new TpHereCommand(kdc));
        addCommand(new TpHereOtherCommand(kdc));

        addCommand(new ChatCommand(kdc));
        addCommand(new AdminCommand(kdc));

        addCommand(new EditorCommand(kdc));
        addCommand(new EditorSaveCommand(kdc));

        /* Management */

        // kingdoms
        addCommand(new CreateCommand(kdc));

        addCommand(new DeleteCommand(kdc));
        addCommand(new DeleteOtherCommand(kdc));

        addCommand(new RenameCommand(kdc));
        addCommand(new RenameOtherCommand(kdc));

        addCommand(new EditDefaultRankCommand(kdc));
        addCommand(new EditDefaultRankOtherCommand(kdc));

        addCommand(new EditDisplayCommand(kdc));
        addCommand(new EditDisplayOtherCommand(kdc));

        addCommand(new EditPrefixCommand(kdc));
        addCommand(new EditPrefixOtherCommand(kdc));

        addCommand(new EditSuffixCommand(kdc));
        addCommand(new EditSuffixOtherCommand(kdc));

        addCommand(new EditInviteOnlyCommand(kdc));
        addCommand(new EditInviteOnlyOtherCommand(kdc));

        addCommand(new EditMaxMembersCommand(kdc));
        addCommand(new EditMaxMembersOtherCommand(kdc));

        // ranks
        addCommand(new RanksListCommand(kdc));
        addCommand(new RanksListOtherCommand(kdc));

        addCommand(new RanksCreateCommand(kdc));
        addCommand(new RanksCreateOtherCommand(kdc));

        addCommand(new RanksRenameCommand(kdc));
        addCommand(new RanksRenameOtherCommand(kdc));

        addCommand(new RanksDeleteCommand(kdc));
        addCommand(new RanksDeleteOtherCommand(kdc));

        addCommand(new RanksEditDisplayCommand(kdc));
        addCommand(new RanksEditDisplayOtherCommand(kdc));

        addCommand(new RanksEditPrefixCommand(kdc));
        addCommand(new RanksEditPrefixOtherCommand(kdc));

        addCommand(new RanksEditSuffixCommand(kdc));
        addCommand(new RanksEditSuffixOtherCommand(kdc));

        addCommand(new RanksEditMaxMembersCommand(kdc));
        addCommand(new RanksEditMaxMembersOtherCommand(kdc));

        addCommand(new RanksEditLevelCommand(kdc));
        addCommand(new RanksEditLevelOtherCommand(kdc));

        addCommand(new RanksCloneCommand(kdc));

        // groups
        addCommand(new GroupsCommand(kdc));

        addCommand(new GroupsListCommand(kdc));
        addCommand(new GroupsListOtherCommand(kdc));

        addCommand(new GroupsAddCommand(kdc));
        addCommand(new GroupsAddOtherCommand(kdc));

        addCommand(new GroupsRemoveCommand(kdc));
        addCommand(new GroupsRemoveOtherCommand(kdc));
    }

}
