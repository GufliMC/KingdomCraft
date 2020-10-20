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
import com.guflan.kingdomcraft.api.domain.RankPermissionGroup;
import com.guflan.kingdomcraft.api.domain.User;
import com.guflan.kingdomcraft.api.entity.PlatformPlayer;
import com.guflan.kingdomcraft.api.entity.PlatformSender;
import com.guflan.kingdomcraft.common.KingdomCraftImpl;
import com.guflan.kingdomcraft.common.command.CommandBaseImpl;
import com.guflan.kingdomcraft.common.permissions.PermissionGroup;

public class RanksCloneCommand extends CommandBaseImpl {

    public RanksCloneCommand(KingdomCraftImpl kdc) {
        super(kdc, "ranks clone", 3, true);
    }

    @Override
    public void execute(PlatformSender sender, String[] args) {
        if ( !sender.hasPermission("kingdom.ranks.create") ) {
            kdc.getMessageManager().send(sender, "noPermission");
            return;
        }

        Kingdom from = kdc.getKingdom(args[0]);
        if ( from == null ) {
            kdc.getMessageManager().send(sender, "cmdDefaultKingdomNotExist", args[0]);
            return;
        }

        Kingdom to = kdc.getKingdom(args[1]);
        if ( to == null ) {
            kdc.getMessageManager().send(sender, "cmdDefaultKingdomNotExist", args[1]);
            return;
        }

        Rank rank = from.getRank(args[2]);
        if ( rank == null ) {
            kdc.getMessageManager().send(sender, "cmdDefaultRankNotExist", args[2]);
            return;
        }

        Rank clone = to.createRank(rank.getName());
        clone.setPrefix(rank.getPrefix());
        clone.setSuffix(rank.getSuffix());
        clone.setDisplay(rank.getDisplay());
        clone.setMaxMembers(rank.getMaxMembers());
        clone.setLevel(rank.getLevel());

        for (RankPermissionGroup rpg : rank.getPermissionGroups() ) {
            clone.createPermissionGroup(rpg.getName());
        }

        kdc.getPlugin().getScheduler().executeAsync(() -> {
            clone.save();
            for (RankPermissionGroup rpg : clone.getPermissionGroups() ) {
                rpg.save();
            }
        });

        kdc.getMessageManager().send(sender, "cmdRanksCloneSuccess", rank.getName(), from.getName(), to.getName());
    }
}
