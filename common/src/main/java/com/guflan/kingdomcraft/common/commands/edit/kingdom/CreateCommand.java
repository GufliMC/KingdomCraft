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

package com.guflan.kingdomcraft.common.commands.edit.kingdom;

import com.guflan.kingdomcraft.api.domain.Kingdom;
import com.guflan.kingdomcraft.api.domain.Rank;
import com.guflan.kingdomcraft.api.domain.User;
import com.guflan.kingdomcraft.api.entity.PlatformSender;
import com.guflan.kingdomcraft.api.entity.PlatformPlayer;
import com.guflan.kingdomcraft.common.KingdomCraftImpl;
import com.guflan.kingdomcraft.common.command.CommandBase;

import java.util.concurrent.CompletableFuture;

public class CreateCommand extends CommandBase {

    public CreateCommand(KingdomCraftImpl kdc) {
        super(kdc, "create", 1);
        setArgumentsHint("<name>");
        setExplanationMessage("cmdCreateExplanation");
        setPermissions("kingdom.create", "kingdom.create.other");
    }

    @Override
    public void execute(PlatformSender sender, String[] args) {
        if ( !args[0].matches("[a-zA-Z0-9]+") ) {
            kdc.getMessageManager().send(sender, "cmdErrorInvalidName");
            return;
        }

        if ( kdc.getKingdom(args[0]) != null ) {
            kdc.getMessageManager().send(sender, "cmdErrorKingdomAlreadyExists", args[0]);
            return;
        }

        if ( sender instanceof PlatformPlayer && kdc.getUser((PlatformPlayer) sender).getKingdom() != null
                && !sender.hasPermission("kingdom.create.other") ) {
            kdc.getMessageManager().send(sender, "cmdErrorNoPermission");
            return;
        }

        Kingdom kingdom = kdc.createKingdom(args[0]);
        CompletableFuture<Void> future = kdc.saveAsync(kingdom);

        kdc.getPlugin().getScheduler().executeSync(() ->
                kdc.getMessageManager().send(sender, "cmdCreate", kingdom.getName()));

        if ( sender instanceof PlatformPlayer && kdc.getUser((PlatformPlayer) sender).getKingdom() == null ) {
            User user = kdc.getUser((PlatformPlayer) sender);
            user.setKingdom(kingdom);
            future.thenRun(user::save);
        }


    }
}
