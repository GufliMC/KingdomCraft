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

import com.guflan.kingdomcraft.api.command.CommandManager;
import com.guflan.kingdomcraft.api.entity.PlatformSender;
import com.guflan.kingdomcraft.bukkit.KingdomCraftBukkitPlugin;
import com.guflan.kingdomcraft.bukkit.commands.InfoCommand;
import com.guflan.kingdomcraft.bukkit.entity.BukkitSender;
import com.guflan.kingdomcraft.common.KingdomCraftImpl;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandHandler implements CommandExecutor, TabCompleter {

    private final KingdomCraftImpl kdc;

    public CommandHandler(KingdomCraftBukkitPlugin plugin) {
        this.kdc = plugin.getKdc();

        CommandManager cm = plugin.getKdc().getCommandManager();
        cm.registerCommand(new InfoCommand(kdc));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if ( sender instanceof Player ) {
            World world = ((Player) sender).getWorld();
            if ( !kdc.getConfig().isWorldEnabled(world.getName()) ) {
                return false;
            }
        }

        if ( args.length == 0 ) {
            // TODO show gui
            return true;
        }

        kdc.getCommandDispatcher().execute(wrap(sender), args);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return kdc.getCommandDispatcher().autocomplete(wrap(sender), args);
    }

    private PlatformSender wrap(CommandSender sender) {
        if ( sender instanceof Player) {
            return kdc.getPlayer(((Player) sender).getUniqueId());
        }
        return new BukkitSender(sender);
    }
}
