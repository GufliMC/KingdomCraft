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
import com.guflan.kingdomcraft.api.domain.User;
import com.guflan.kingdomcraft.api.entity.PlatformSender;
import com.guflan.kingdomcraft.api.entity.PlatformPlayer;
import com.guflan.kingdomcraft.common.KingdomCraftImpl;
import com.guflan.kingdomcraft.common.command.CommandBase;

public class RanksCreateCommand extends CommandBase {

    public RanksCreateCommand(KingdomCraftImpl kdc) {
        super(kdc, "ranks create", 1, true);
        setArgumentsHint("<name>");
        setExplanationMessage("cmdRanksCreateExplanation");
        setPermissions("kingdom.ranks.create");
    }

    @Override
    public void execute(PlatformSender sender, String[] args) {
        if ( !args[0].matches("[a-zA-Z0-9]+") ) {
            kdc.getMessageManager().send(sender, "cmdErrorInvalidName");
            return;
        }

        User user = kdc.getUser((PlatformPlayer) sender);
        Kingdom kingdom = user.getKingdom();
        if ( kingdom == null ) {
            kdc.getMessageManager().send(sender, "cmdErrorSenderNoKingdom");
            return;
        }

        if ( kingdom.getRank(args[0]) != null ) {
            kdc.getMessageManager().send(sender, "cmdErrorRankAlreadyexists", args[0]);
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

        kdc.getMessageManager().send(sender, "cmdRanksCreate", rank.getName());
    }
}
