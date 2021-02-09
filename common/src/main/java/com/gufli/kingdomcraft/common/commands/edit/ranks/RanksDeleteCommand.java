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
import com.gufli.kingdomcraft.api.entity.PlatformSender;
import com.gufli.kingdomcraft.api.entity.PlatformPlayer;
import com.gufli.kingdomcraft.common.KingdomCraftImpl;
import com.gufli.kingdomcraft.common.command.CommandBase;

import java.util.List;

public class RanksDeleteCommand extends CommandBase {

    public RanksDeleteCommand(KingdomCraftImpl kdc) {
        super(kdc, "ranks delete", 1, true);
        setArgumentsHint("<rank>");
        setExplanationMessage("cmdRanksDeleteExplanation");
        setPermissions("kingdom.ranks.delete");
    }

    @Override
    public List<String> autocomplete(PlatformPlayer sender, String[] args) {
        // DONT ADD AUTOCOMPLETE HERE TO PREVENT AN ACCIDENTAL WRONG COMPLETION
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

        if ( kingdom.getDefaultRank().equals(rank) ) {
            kdc.getMessages().send(sender, "cmdRanksDeleteDefault");
        } else {
            kdc.getMessages().send(sender, "cmdRanksDelete", rank.getName());
        }

        kdc.deleteAsync(rank);
    }
}
