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

package com.guflan.kingdomcraft.common.commands.edit.ranks;

import com.guflan.kingdomcraft.api.domain.Kingdom;
import com.guflan.kingdomcraft.api.domain.Rank;
import com.guflan.kingdomcraft.api.entity.PlatformPlayer;
import com.guflan.kingdomcraft.api.entity.PlatformSender;
import com.guflan.kingdomcraft.common.KingdomCraftImpl;
import com.guflan.kingdomcraft.common.command.CommandBase;

import java.util.List;

public class RanksDeleteOtherCommand extends CommandBase {

    public RanksDeleteOtherCommand(KingdomCraftImpl kdc) {
        super(kdc, "ranks delete", 2);
        setArgumentsHint("<kingdom> <rank>");
        setExplanationMessage("cmdRanksDeleteOtherExplanation");
        setPermissions("kingdom.ranks.delete.other");
    }

    @Override
    public List<String> autocomplete(PlatformPlayer sender, String[] args) {
        // DONT ADD AUTOCOMPLETE HERE TO PREVENT AN ACCIDENTAL WRONG COMPLETION
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
            kdc.getMessageManager().send(sender, "cmdErrorRankNotExist", args[1]);
            return;
        }

        kdc.getMessageManager().send(sender, "cmdRanksDelete", rank.getName());
        if ( kingdom.getDefaultRank().equals(rank) ) {
            kdc.getMessageManager().send(sender, "cmdRanksDeleteOtherDefault", kingdom.getName());
        }

        kdc.deleteAsync(rank);
    }
}
