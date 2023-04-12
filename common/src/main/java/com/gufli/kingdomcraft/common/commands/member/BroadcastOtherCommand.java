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

package com.gufli.kingdomcraft.common.commands.member;

import com.gufli.kingdomcraft.api.domain.Kingdom;
import com.gufli.kingdomcraft.api.entity.PlatformPlayer;
import com.gufli.kingdomcraft.api.entity.PlatformSender;
import com.gufli.kingdomcraft.common.KingdomCraftImpl;
import com.gufli.kingdomcraft.common.command.CommandBase;

import java.util.Set;
import java.util.stream.Collectors;

public class BroadcastOtherCommand extends CommandBase {

    public BroadcastOtherCommand(KingdomCraftImpl kdc) {
        super(kdc, "broadcast", 2, true);
        setArgumentsHint("<kingdom> <message>");
        setExplanationMessage(() -> kdc.getMessages().getMessage("cmdBroadcastExplanation"));
        setPermissions("kingdom.broadcast.other");
    }

    @Override
    public void execute(PlatformSender sender, String[] args) {
        Kingdom kingdom = kdc.getKingdom(args[0]);
        if (kingdom == null || kingdom.isTemplate()) {
            kdc.getMessages().send(sender, "cmdErrorKingdomNotExist", args[0]);
            return;
        }

        Set<PlatformSender> target = kdc.getOnlinePlayers().stream().filter(p -> p.getUser().getKingdom() == kingdom)
                .collect(Collectors.toSet());
        target.add(sender);

        String msg = kdc.getMessages().colorify(args[1]);
        target.forEach(p -> kdc.getMessages().send(p, "cmdBroadcast", msg));
    }
}
