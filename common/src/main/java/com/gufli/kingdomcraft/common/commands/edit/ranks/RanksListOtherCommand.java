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
import com.gufli.kingdomcraft.api.entity.PlatformPlayer;
import com.gufli.kingdomcraft.api.entity.PlatformSender;
import com.gufli.kingdomcraft.common.KingdomCraftImpl;
import com.gufli.kingdomcraft.common.command.CommandBase;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class RanksListOtherCommand extends CommandBase {

    public RanksListOtherCommand(KingdomCraftImpl kdc) {
        super(kdc, "ranks list", 1);
        setArgumentsHint("<kingdom>");
        setExplanationMessage(() -> kdc.getMessages().getMessage("cmdRanksListOtherExplanation"));
        setPermissions("kingdom.ranks.list.other");
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
        Kingdom kingdom = kdc.getKingdom(args[0]);
        if ( kingdom == null ) {
            kdc.getMessages().send(sender, "cmdErrorKingdomNotExist", args[0]);
            return;
        }

        String list = kingdom.getRanks().stream()
                .sorted(Comparator.comparingInt(Rank::getLevel).reversed())
                .map(Rank::getName)
                .collect(Collectors.joining(", "));
        kdc.getMessages().send(sender, "cmdRanksListOther", kingdom.getName(), list);
    }
}
