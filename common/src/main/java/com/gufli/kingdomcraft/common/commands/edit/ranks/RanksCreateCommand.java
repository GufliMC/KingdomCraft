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

public class RanksCreateCommand extends CommandBase {

    public RanksCreateCommand(KingdomCraftImpl kdc) {
        super(kdc, "ranks create", 1, true);
        setArgumentsHint("<name>");
        setExplanationMessage(() -> kdc.getMessages().getMessage("cmdRanksCreateExplanation"));
        setPermissions("kingdom.ranks.create");
    }

    @Override
    public void execute(PlatformSender sender, String[] args) {
        if ( !args[0].matches("[a-zA-Z0-9]+") ) {
            kdc.getMessages().send(sender, "cmdErrorInvalidName");
            return;
        }

        User user = kdc.getUser((PlatformPlayer) sender);
        Kingdom kingdom = user.getKingdom();
        if ( kingdom == null ) {
            kdc.getMessages().send(sender, "cmdErrorSenderNoKingdom");
            return;
        }

        if ( kingdom.getRank(args[0]) != null ) {
            kdc.getMessages().send(sender, "cmdErrorRankAlreadyexists", args[0]);
            return;
        }

        Rank rank = kingdom.createRank(args[0]);
        if ( kingdom.getDefaultRank() == null ) {
            kingdom.setDefaultRank(rank);
        }

        kdc.saveAsync(rank).thenRun(() -> {
            if ( kingdom.getDefaultRank() == rank ) {
                kdc.saveAsync(kingdom);
            }
        });

        kdc.getMessages().send(sender, "cmdRanksCreate", rank.getName());
    }
}
