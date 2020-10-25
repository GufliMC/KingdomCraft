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
import com.guflan.kingdomcraft.api.domain.Rank;
import com.guflan.kingdomcraft.api.domain.User;
import com.guflan.kingdomcraft.api.entity.PlatformSender;
import com.guflan.kingdomcraft.api.entity.PlatformPlayer;
import com.guflan.kingdomcraft.common.KingdomCraftImpl;
import com.guflan.kingdomcraft.common.command.CommandBaseImpl;

public class CreateCommand extends CommandBaseImpl {

    public CreateCommand(KingdomCraftImpl kdc) {
        super(kdc, "create", 1);
        setArgumentsHint("<name>");
        setExplanationMessage(kdc.getMessageManager().getMessage("cmdCreateExplanation"));
        setPermissions("kingdom.create", "kingdom.create.other");
    }

    @Override
    public void execute(PlatformSender sender, String[] args) {
        if ( !args[0].matches("[a-zA-Z0-9]+") ) {
            kdc.getMessageManager().send(sender, "cmdCreateNameInvalid");
            return;
        }

        if ( kdc.getKingdom(args[0]) != null ) {
            kdc.getMessageManager().send(sender, "cmdCreateAlreadyExists", args[0]);
            return;
        }

        if ( sender instanceof PlatformPlayer && kdc.getUser((PlatformPlayer) sender).getKingdom() != null
                && !sender.hasPermission("kingdom.create.other") ) {
            kdc.getMessageManager().send(sender, "cmdErrorNoPermission");
            return;
        }

        kdc.getPlugin().getScheduler().executeAsync(() -> {
            Kingdom kingdom = kdc.createKingdom(args[0]);
            kingdom.save();

            Rank king = kingdom.createRank("king");
            king.setLevel(99);
            king.save();

            kdc.getPlugin().getScheduler().executeSync(() ->
                    kdc.getMessageManager().send(sender, "cmdCreateSuccess", kingdom.getName()));

            if ( sender instanceof PlatformPlayer && kdc.getUser((PlatformPlayer) sender).getKingdom() == null ) {
                User user = kdc.getUser((PlatformPlayer) sender);
                user.setKingdom(kingdom);
                user.setRank(king);
                user.save();
                return;
            }
        });
    }
}
