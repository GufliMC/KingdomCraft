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

package com.gufli.kingdomcraft.common.commands.general;

import com.gufli.kingdomcraft.api.domain.Kingdom;
import com.gufli.kingdomcraft.api.entity.PlatformSender;
import com.gufli.kingdomcraft.common.KingdomCraftImpl;
import com.gufli.kingdomcraft.common.command.CommandBase;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ListCommand extends CommandBase {

    public ListCommand(KingdomCraftImpl kdc) {
        super(kdc, "list", 0);
        setExplanationMessage("cmdListExplanation");
        setPermissions("kingdom.list");
    }

    @Override
    public void execute(PlatformSender sender, String[] args) {
        List<String> kingdoms = kdc.getKingdoms().stream()
                .sorted(Comparator.comparing(Kingdom::isInviteOnly))
                .map(Kingdom::getName)
                .collect(Collectors.toList());

        String delimiter = kdc.getMessageManager().getMessage("cmdListDelimiter");
        delimiter = delimiter != null ? delimiter : "&a, &2";

        String result = String.join(delimiter, kingdoms);
        kdc.getMessageManager().send(sender, "cmdList", result);
    }
}
