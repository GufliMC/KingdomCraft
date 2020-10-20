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

import com.guflan.kingdomcraft.api.domain.User;
import com.guflan.kingdomcraft.api.entity.PlatformPlayer;
import com.guflan.kingdomcraft.api.entity.PlatformSender;
import com.guflan.kingdomcraft.common.KingdomCraftImpl;
import com.guflan.kingdomcraft.common.command.CommandBaseImpl;

public class InfoCommand extends CommandBaseImpl {

    public InfoCommand(KingdomCraftImpl kdc) {
        super(kdc, "info", 0, true);
        setArgumentsHint("<[player/kingdom]>");
        setExplanationMessage(kdc.getMessageManager().getMessage("cmdInfoExplanation"));
        setPermissions("kingdom.info");
    }

    @Override
    public void execute(PlatformSender sender, String[] args) {

        User user = kdc.getUser((PlatformPlayer) sender);
        sender.sendMessage("kingdom: " + (user.getKingdom() == null ? "null" : user.getKingdom().getDisplay()));
        sender.sendMessage("rank: " + (user.getRank() == null ? "null" : user.getRank().getDisplay()));

        // TODO
    }
}
