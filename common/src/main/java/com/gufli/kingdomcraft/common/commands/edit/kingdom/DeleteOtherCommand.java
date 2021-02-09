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

package com.gufli.kingdomcraft.common.commands.edit.kingdom;

import com.gufli.kingdomcraft.api.domain.Kingdom;
import com.gufli.kingdomcraft.api.domain.User;
import com.gufli.kingdomcraft.api.entity.PlatformPlayer;
import com.gufli.kingdomcraft.api.entity.PlatformSender;
import com.gufli.kingdomcraft.common.KingdomCraftImpl;
import com.gufli.kingdomcraft.common.command.CommandBase;

import java.util.List;

public class DeleteOtherCommand extends CommandBase {

    public final static String DELETE_KEY = "KINGDOM_DELETE_REQUEST";

    public DeleteOtherCommand(KingdomCraftImpl kdc) {
        super(kdc, "delete", 1);
        setArgumentsHint("<kingdom>");
        setExplanationMessage("cmdDeleteOtherExplanation");
        setPermissions("kingdom.delete", "kingdom.delete.other");
    }

    @Override
    public List<String> autocomplete(PlatformPlayer player, String[] args) {
        // DONT ADD AUTOCOMPLETE HERE TO PREVENT AN ACCIDENTAL WRONG COMPLETION
        return null;
    }

    @Override
    public void execute(PlatformSender sender, String[] args) {
        if ( sender instanceof PlatformPlayer && args[0].equalsIgnoreCase("confirm") ) {
            PlatformPlayer player = (PlatformPlayer) sender;
            if ( !player.has(DELETE_KEY) ) {
                kdc.getMessages().send(sender, "cmdErrorKingdomNotExist", args[0]);
                return;
            }

            DeleteRequest req = player.get(DELETE_KEY, DeleteRequest.class);
            if ( System.currentTimeMillis() - req.timestamp > 1000 * 60 ) {
                kdc.getMessages().send(sender, "cmdErrorKingdomNotExist", args[0]);
                return;
            }

            delete(player, req.kingdom);
            player.remove(DELETE_KEY);
            return;
        }

        Kingdom kingdom = kdc.getKingdom(args[0]);
        if ( kingdom == null || kingdom.isTemplate() ) {
            kdc.getMessages().send(sender, "cmdErrorKingdomNotExist", args[0]);
            return;
        }

        if ( sender instanceof PlatformPlayer ) {
            User user = kdc.getUser((PlatformPlayer) sender);
            if ( user.getKingdom() != kingdom && !sender.hasPermission("kingdom.delete.other")) {
                kdc.getMessages().send(sender, "noPermission");
                return;
            }
        }

        if ( !(sender instanceof PlatformPlayer) ) {
            delete(sender, kingdom);
        } else {
            ((PlatformPlayer) sender).set(DELETE_KEY, new DeleteRequest(kingdom));
            kdc.getMessages().send(sender, "cmdDeleteConfirm", kingdom.getName());
        }
    }

    private void delete(PlatformSender sender, Kingdom kingdom) {
        for (PlatformPlayer p : kdc.getOnlinePlayers()) {
            User user = kdc.getUser(p);
            if ( user.getKingdom() == kingdom) {
                user.setKingdom(null);
                kdc.getMessages().send(p, "cmdDeleteMembers");
            }
        }

        kdc.deleteAsync(kingdom);
        kdc.getMessages().send(sender, "cmdDelete", kingdom.getName());
    }

    public static class DeleteRequest {

        final Kingdom kingdom;
        final long timestamp;

        public DeleteRequest(Kingdom kingdom) {
            this.kingdom = kingdom;
            this.timestamp = System.currentTimeMillis();
        }

    }
}
