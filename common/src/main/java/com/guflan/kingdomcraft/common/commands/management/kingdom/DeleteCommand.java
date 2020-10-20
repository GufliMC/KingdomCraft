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

package com.guflan.kingdomcraft.common.commands.management.kingdom;

import com.guflan.kingdomcraft.api.domain.Kingdom;
import com.guflan.kingdomcraft.api.domain.User;
import com.guflan.kingdomcraft.api.entity.PlatformSender;
import com.guflan.kingdomcraft.api.entity.PlatformPlayer;
import com.guflan.kingdomcraft.common.KingdomCraftImpl;
import com.guflan.kingdomcraft.common.command.CommandBaseImpl;

public class DeleteCommand extends CommandBaseImpl {

    public DeleteCommand(KingdomCraftImpl kdc) {
        super(kdc, "delete", 1);
        setArgumentsHint("<kingdom>");
        setExplanationMessage(kdc.getMessageManager().getMessage("cmdDeleteExplanation"));
        setPermissions("kingdom.delete", "kingdom.delete.other");
    }

    @Override
    public void execute(PlatformSender sender, String[] args) {
        Kingdom kingdom = kdc.getKingdom(args[0]);
        if ( kingdom == null ) {
            kdc.getMessageManager().send(sender, "cmdErrorKingdomNotExist", args[0]);
            return;
        }

        if ( sender instanceof PlatformPlayer) {
            User user = kdc.getUser((PlatformPlayer) sender);

            if ( user.getKingdom() != kingdom && !sender.hasPermission("kingdom.delete.other")) {
                kdc.getMessageManager().send(sender, "noPermission");
                return;
            }
        }

        for ( PlatformPlayer p : kdc.getOnlinePlayers() ) {
            if ( p.equals(sender) || kdc.getUser(p).getKingdom() != kingdom ) continue;
            kdc.getMessageManager().send(p, "cmdDeleteSuccessMembers");
        }

        // async deleting
        kdc.getPlugin().getScheduler().executeAsync(kingdom::delete);

        kdc.getMessageManager().send(sender, "cmdDeleteSuccess", kingdom.getName());
    }
}
