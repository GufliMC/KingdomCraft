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

import java.util.List;
import java.util.stream.Collectors;

public class RanksCreateOtherCommand extends CommandBase {

    public RanksCreateOtherCommand(KingdomCraftImpl kdc) {
        super(kdc, "ranks create", 2);
        setArgumentsHint("<kingdom> <name>");
        setExplanationMessage(kdc.getMessageManager().getMessage("cmdRanksCreateOtherExplanation"));
        setPermissions("kingdom.ranks.create.other");
    }

    @Override
    public List<String> autocomplete(PlatformPlayer sender, String[] args) {
        if ( args.length == 1 ) {
            return kdc.getKingdoms().stream().map(Kingdom::getName).collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public void execute(PlatformSender sender, String[] args) {
        if ( !args[1].matches("[a-zA-Z0-9]+") ) {
            kdc.getMessageManager().send(sender, "cmdErrorNameInvalid");
            return;
        }

        Kingdom kingdom = kdc.getKingdom(args[0]);
        if ( kingdom == null ) {
            kdc.getMessageManager().send(sender, "cmdErrorKingdomNotExist", args[0]);
            return;
        }

        if ( kingdom.getRank(args[1]) != null ) {
            kdc.getMessageManager().send(sender, "cmdErrorRankAlreadyExists", args[1]);
            return;
        }

        Rank rank = kingdom.createRank(args[1]);
        if ( kingdom.getDefaultRank() == null ) {
            kingdom.setDefaultRank(rank);
        }

        kdc.saveAsync(rank).thenRun(() -> {
            if ( kingdom.getDefaultRank() == rank ) {
                kdc.saveAsync(kingdom);
            }
        });

        kdc.getMessageManager().send(sender, "cmdRanksCreateOther", rank.getName(), kingdom.getName());
    }
}
