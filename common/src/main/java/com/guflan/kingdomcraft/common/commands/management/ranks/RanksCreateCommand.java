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
import com.guflan.kingdomcraft.common.AbstractKingdomCraft;
import com.guflan.kingdomcraft.common.command.CommandBaseImpl;

public class RanksCreateCommand extends CommandBaseImpl {

    public RanksCreateCommand(AbstractKingdomCraft kdc) {
        super(kdc, "ranks create", 1, true);
    }

    @Override
    public void execute(PlatformSender sender, String[] args) {
        if ( !sender.hasPermission("kingdom.ranks.create") ) {
            kdc.getMessageManager().send(sender, "noPermission");
            return;
        }

        if ( !args[0].matches("[a-zA-Z0-9]+") ) {
            kdc.getMessageManager().send(sender, "cmdRanksCreateNameInvalid");
            return;
        }

        User user = kdc.getUser((PlatformPlayer) sender);
        Kingdom kingdom = user.getKingdom();
        if ( kingdom == null ) {
            kdc.getMessageManager().send(sender, "cmdDefaultSenderNoKingdom");
            return;
        }

        if ( kingdom.getRank(args[0]) != null ) {
            kdc.getMessageManager().send(sender, "cmdRanksCreateAlreadyExists", args[0]);
            return;
        }

        Rank rank = kingdom.createRank(args[0]);

        if ( kingdom.getDefaultRank() == null ) {
            kingdom.setDefaultRank(rank);
        }
        kdc.save(rank.getKingdom());

        /*
        kdc.save(rank.getKingdom()).thenRun(() -> {
            if (kingdom.getDefaultRank() == null) {
                kingdom.setDefaultRank(rank);
                kdc.save(kingdom);
            }
        });
         */

        kdc.getMessageManager().send(sender, "cmdRanksCreateSuccess", rank.getName());
    }
}
