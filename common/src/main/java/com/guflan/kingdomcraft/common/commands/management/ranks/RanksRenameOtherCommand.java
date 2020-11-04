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

package com.guflan.kingdomcraft.common.commands.management.ranks;

import com.guflan.kingdomcraft.api.domain.Kingdom;
import com.guflan.kingdomcraft.api.domain.Rank;
import com.guflan.kingdomcraft.api.entity.PlatformPlayer;
import com.guflan.kingdomcraft.api.entity.PlatformSender;
import com.guflan.kingdomcraft.common.KingdomCraftImpl;
import com.guflan.kingdomcraft.common.command.CommandBase;
import com.guflan.kingdomcraft.common.ebean.beans.BRank;

import java.util.List;
import java.util.stream.Collectors;

public class RanksRenameOtherCommand extends CommandBase {

    public RanksRenameOtherCommand(KingdomCraftImpl kdc) {
        super(kdc, "ranks rename", 3);
        setArgumentsHint("<kingdom> <rank> <name>");
        setExplanationMessage(kdc.getMessageManager().getMessage("cmdRanksRenameOtherExplanation"));
        setPermissions("kingdom.ranks.rename.other");
    }

    @Override
    public List<String> autocomplete(PlatformPlayer sender, String[] args) {
        if ( args.length == 1 ) {
            return kdc.getKingdoms().stream().map(Kingdom::getName).collect(Collectors.toList());
        }
        if ( args.length == 2 ) {
            Kingdom kingdom = kdc.getKingdom(args[0]);
            if ( kingdom == null ) {
                return null;
            }
            return kingdom.getRanks().stream().map(Rank::getName).collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public void execute(PlatformSender sender, String[] args) {
        Kingdom kingdom = kdc.getKingdom(args[0]);
        if ( kingdom == null ) {
            kdc.getMessageManager().send(sender, "cmdErrorKingdomNotExist", args[0]);
            return;
        }

        Rank rank = kingdom.getRank(args[1]);
        if ( rank == null ) {
            kdc.getMessageManager().send(sender, "cmdErrorRankNotExist", args[0]);
            return;
        }

        if ( !args[2].matches("[a-zA-Z0-9]+") ) {
            kdc.getMessageManager().send(sender, "cmdErrorNameInvalid");
            return;
        }

        if ( kingdom.getRank(args[2]) != null ) {
            kdc.getMessageManager().send(sender, "cmdErrorRankAlreadyExists", args[2]);
            return;
        }

        // async saving
        String oldName = rank.getName();
        ((BRank) rank).name = args[2];
        kdc.saveAsync(rank);

        kdc.getMessageManager().send(sender, "cmdRanksRename", oldName, rank.getName(), kingdom.getName());
    }
}
