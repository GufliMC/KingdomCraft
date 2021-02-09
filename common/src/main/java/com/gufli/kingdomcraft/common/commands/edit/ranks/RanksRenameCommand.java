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

package com.gufli.kingdomcraft.common.commands.edit.ranks;

import com.gufli.kingdomcraft.api.domain.Kingdom;
import com.gufli.kingdomcraft.api.domain.Rank;
import com.gufli.kingdomcraft.api.domain.User;
import com.gufli.kingdomcraft.api.entity.PlatformPlayer;
import com.gufli.kingdomcraft.api.entity.PlatformSender;
import com.gufli.kingdomcraft.common.KingdomCraftImpl;
import com.gufli.kingdomcraft.common.command.CommandBase;

import java.util.List;
import java.util.stream.Collectors;

public class RanksRenameCommand extends CommandBase {

    public RanksRenameCommand(KingdomCraftImpl kdc) {
        super(kdc, "ranks rename", 2, true);
        setArgumentsHint("<rank> <name>");
        setExplanationMessage("cmdRanksRenameExplanation");
        setPermissions("kingdom.ranks.rename");
    }

    @Override
    public List<String> autocomplete(PlatformPlayer player, String[] args) {
        if ( args.length == 1 ) {
            User user = kdc.getUser(player);
            if ( user.getKingdom() == null ) {
                return null;
            }
            return user.getKingdom().getRanks().stream().map(Rank::getName).collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public void execute(PlatformSender sender, String[] args) {
        User user = kdc.getUser((PlatformPlayer) sender);
        Kingdom kingdom = user.getKingdom();
        if ( kingdom == null ) {
            kdc.getMessages().send(sender, "cmdErrorSenderNoKingdom");
            return;
        }

        Rank rank = kingdom.getRank(args[0]);
        if ( rank == null ) {
            kdc.getMessages().send(sender, "cmdErrorRankNotExist", args[0]);
            return;
        }

        if ( !args[1].matches("[a-zA-Z0-9]+") ) {
            kdc.getMessages().send(sender, "cmdErrorInvalidName");
            return;
        }

        if ( kingdom.getRank(args[1]) != null ) {
            kdc.getMessages().send(sender, "cmdErrorRankAlreadyexists", args[0]);
            return;
        }

        String oldName = rank.getName();
        rank.renameTo(args[1]);
        kdc.saveAsync(rank);

        kdc.getMessages().send(sender, "cmdRanksRename", oldName, rank.getName());
    }
}
