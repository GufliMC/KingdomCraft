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

package com.guflan.kingdomcraft.common.commands;

import com.guflan.kingdomcraft.api.domain.Kingdom;
import com.guflan.kingdomcraft.api.entity.PlatformSender;
import com.guflan.kingdomcraft.common.KingdomCraftImpl;
import com.guflan.kingdomcraft.common.command.CommandBaseImpl;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ListCommand extends CommandBaseImpl {

    public ListCommand(KingdomCraftImpl kdc) {
        super(kdc, "list", 0);
        setExplanationMessage(kdc.getMessageManager().getMessage("cmdListExplanation"));
        setPermissions("kingdom.list");
    }

    @Override
    public void execute(PlatformSender sender, String[] args) {
        List<String> kingdoms = kdc.getKingdoms().stream()
                .sorted(Comparator.comparing(Kingdom::isInviteOnly))
                .map(k -> (k.isInviteOnly() ? "&c" : "&a") + k.getName())
                .collect(Collectors.toList());

        kdc.getMessageManager().send(sender, "cmdList", String.join("&f, ", kingdoms));
    }
}
