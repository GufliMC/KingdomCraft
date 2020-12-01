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

package com.gufli.kingdomcraft.common.commands.tphere;

import com.gufli.kingdomcraft.api.domain.Kingdom;
import com.gufli.kingdomcraft.api.domain.User;
import com.gufli.kingdomcraft.api.entity.PlatformLocation;
import com.gufli.kingdomcraft.api.entity.PlatformPlayer;
import com.gufli.kingdomcraft.api.entity.PlatformSender;
import com.gufli.kingdomcraft.common.KingdomCraftImpl;
import com.gufli.kingdomcraft.common.command.CommandBase;

import java.util.List;
import java.util.stream.Collectors;

public class TpHereOtherCommand extends CommandBase {

    public TpHereOtherCommand(KingdomCraftImpl kdc) {
        super(kdc, "tphere", 1, true);
        setArgumentsHint("<kingdom>");
        setExplanationMessage("cmdTpHereOtherExplanation");
        setPermissions("kingdom.tphere.other");
    }

    @Override
    public List<String> autocomplete(PlatformPlayer player, String[] args) {
        return kdc.getKingdoms().stream().map(Kingdom::getName).collect(Collectors.toList());
    }

    @Override
    public void execute(PlatformSender sender, String[] args) {
        PlatformPlayer player = (PlatformPlayer) sender;

        Kingdom kingdom = kdc.getKingdom(args[0]);
        if ( kingdom == null ) {
            kdc.getMessageManager().send(sender, "cmdErrorKingdomNotExist", args[0]);
            return;
        }

        PlatformLocation loc = player.getLocation();
        for ( PlatformPlayer p : kdc.getOnlinePlayers() ) {
            if ( p == sender) {
                continue;
            }

            User pu = kdc.getUser(p);
            if ( pu.getKingdom() == kingdom ) {
                p.teleport(loc);
                kdc.getMessageManager().send(p, "cmdTpHereTarget", player.getName());
            }
        }

        kdc.getMessageManager().send(player, "cmdTpHereOther", kingdom.getName());
    }
}
