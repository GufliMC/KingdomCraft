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

package com.gufli.kingdomcraft.bukkit.commands;

import com.gufli.kingdomcraft.api.entity.PlatformSender;
import com.gufli.kingdomcraft.bukkit.KingdomCraftBukkitPlugin;
import com.gufli.kingdomcraft.common.command.CommandBase;

public class ReloadCommand extends CommandBase {

    private final KingdomCraftBukkitPlugin plugin;

    public ReloadCommand(KingdomCraftBukkitPlugin plugin) {
        super(plugin.getKdc(), "reload", 0);
        setExplanationMessage("cmdReloadExplanation");
        setPermissions("kingdom.reload");
        this.plugin = plugin;
    }

    @Override
    public void execute(PlatformSender sender, String[] args) {
        plugin.load();
        kdc.getMessages().send(sender, "cmdReload");
    }
}
