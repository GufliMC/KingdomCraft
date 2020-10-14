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

package com.guflan.kingdomcraft.bukkit.command;

import com.guflan.kingdomcraft.api.KingdomCraft;
import com.guflan.kingdomcraft.bukkit.BukkitKingdomCraftPlugin;
import com.guflan.kingdomcraft.bukkit.entity.BukkitCommandSender;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.List;

public class BukkitCommandExecutor implements CommandExecutor, TabCompleter {

    private final BukkitKingdomCraftPlugin plugin;

    public BukkitCommandExecutor(BukkitKingdomCraftPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if ( sender instanceof Player ) {
            World world = ((Player) sender).getWorld();
            if ( !plugin.getConfiguration().isWorldEnabled(world.getName()) ) {
                // TODO
                return false;
            }
        }

        if ( args.length == 0 ) {
            // TODO show gui
            return true;
        }

        KingdomCraft.getCommandManager().execute(wrap(sender), args);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return KingdomCraft.getCommandManager().autocomplete(wrap(sender), args);
    }

    private com.guflan.kingdomcraft.api.entity.CommandSender wrap(CommandSender sender) {
        if ( sender instanceof Player) {
            return KingdomCraft.getPlayer(((Player) sender).getUniqueId());
        }
        return new BukkitCommandSender(sender);
    }
}
