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
import com.guflan.kingdomcraft.api.entity.PlatformSender;
import com.guflan.kingdomcraft.common.KingdomCraftImpl;
import com.guflan.kingdomcraft.common.command.CommandBaseImpl;

public class RanksEditPrefixOtherCommand extends CommandBaseImpl {

    public RanksEditPrefixOtherCommand(KingdomCraftImpl kdc) {
        super(kdc, "ranks edit prefix", 3);
    }

    @Override
    public void execute(PlatformSender sender, String[] args) {
        if ( !sender.hasPermission("kingdom.ranks.edit.prefix.other") ) {
            kdc.getMessageManager().send(sender, "noPermission");
            return;
        }

        Kingdom kingdom = kdc.getKingdom(args[0]);
        if ( kingdom == null ) {
            kdc.getMessageManager().send(sender, "cmdDefaultKingdomNotExist", args[0]);
            return;
        }

        Rank rank = kingdom.getRank(args[1]);
        if ( rank == null ) {
            kdc.getMessageManager().send(sender, "cmdDefaultRankNotExist", args[1]);
            return;
        }

        rank.setPrefix(args[2]);

        // async saving
        kdc.getPlugin().getScheduler().executeAsync(rank::save);

        kdc.getMessageManager().send(sender, "cmdRanksEditOtherSuccess", "prefix",
                kingdom.getName(), rank.getName(), args[2]);
    }
}
