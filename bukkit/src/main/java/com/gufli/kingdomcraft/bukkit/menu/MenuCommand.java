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

package com.gufli.kingdomcraft.bukkit.menu;

import com.gufli.kingdomcraft.api.entity.PlatformPlayer;
import com.gufli.kingdomcraft.api.entity.PlatformSender;
import com.gufli.kingdomcraft.common.KingdomCraftImpl;
import com.gufli.kingdomcraft.common.command.CommandBase;

public class MenuCommand extends CommandBase {

    public MenuCommand(KingdomCraftImpl kdc) {
        super(kdc, "menu", 0, true);
        addCommand("panel");
        setExplanationMessage(() -> kdc.getMessages().getMessage("cmdMenuExplanation"));
        setPermissions("kingdom.menu");
        KingdomMenu.kdc = kdc;
    }

    @Override
    public void execute(PlatformSender sender, String[] args) {
        KingdomMenu.open((PlatformPlayer) sender);
    }


}
