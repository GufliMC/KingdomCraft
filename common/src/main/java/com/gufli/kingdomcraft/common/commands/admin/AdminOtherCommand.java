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

package com.gufli.kingdomcraft.common.commands.admin;

import com.gufli.kingdomcraft.api.entity.PlatformPlayer;
import com.gufli.kingdomcraft.api.entity.PlatformSender;
import com.gufli.kingdomcraft.common.KingdomCraftImpl;
import com.gufli.kingdomcraft.common.command.CommandBase;

import java.util.List;
import java.util.stream.Collectors;

public class AdminOtherCommand extends CommandBase {

    public AdminOtherCommand(KingdomCraftImpl kdc) {
        super(kdc, "admin", 1);
        setArgumentsHint("<player>");
        setExplanationMessage(() -> kdc.getMessages().getMessage("cmdAdminExplanation"));
        setPermissions("kingdom.admin.other");
    }

    @Override
    public List<String> autocomplete(PlatformPlayer sender, String[] args) {
        if ( args.length == 1 ) {
            return kdc.getOnlinePlayers().stream().map(PlatformPlayer::getName).collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public void execute(PlatformSender sender, String[] args) {
        PlatformPlayer target = kdc.getPlayer(args[0]);
        if ( target == null ) {
            kdc.getMessages().send(sender, "cmdErrorNotOnline", args[0]);
            return;
        }

        if ( target.isAdmin() ) {
            target.setAdmin(false);
            kdc.getMessages().send(target, "cmdAdminDisableTarget");
            kdc.getMessages().send(sender, "cmdAdminDisableSender", target.getName());
        } else {
            target.setAdmin(true);
            kdc.getMessages().send(target, "cmdAdminEnableTarget");
            kdc.getMessages().send(sender, "cmdAdminEnableSender", target.getName());
        }
    }
}
