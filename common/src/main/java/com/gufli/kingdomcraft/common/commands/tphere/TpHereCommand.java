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

public class TpHereCommand extends CommandBase {

    public TpHereCommand(KingdomCraftImpl kdc) {
        super(kdc, "tphere", 0, true);
        setExplanationMessage(() -> kdc.getMessages().getMessage("cmdTpHereExplanation"));
        setPermissions("kingdom.tphere");
    }

    @Override
    public void execute(PlatformSender sender, String[] args) {
        PlatformPlayer player = (PlatformPlayer) sender;

        User user = kdc.getUser(player);
        Kingdom kingdom = user.getKingdom();
        if ( kingdom == null ) {
            kdc.getMessages().send(sender, "cmdErrorSenderNoKingdom");
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
                kdc.getMessages().send(p, "cmdTpHereTarget", player.getName());
            }
        }

        kdc.getMessages().send(player, "cmdTpHere");
    }
}
