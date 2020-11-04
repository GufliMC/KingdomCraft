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

package com.guflan.kingdomcraft.common.commands.admin;

import com.guflan.kingdomcraft.api.entity.PlatformSender;
import com.guflan.kingdomcraft.common.KingdomCraftImpl;
import com.guflan.kingdomcraft.common.command.CommandBase;

public class ReloadCommand extends CommandBase {

    public ReloadCommand(KingdomCraftImpl kdc) {
        super(kdc, "reload", 0);
        setExplanationMessage(kdc.getMessageManager().getMessage("cmdReloadExplanation"));
        setPermissions("kingdom.reload");
    }

    @Override
    public void execute(PlatformSender sender, String[] args) {
        kdc.getPlugin().reload();
        kdc.getMessageManager().send(sender, "cmdReload");
    }
}
