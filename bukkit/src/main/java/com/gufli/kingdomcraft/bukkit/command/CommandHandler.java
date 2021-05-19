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

package com.gufli.kingdomcraft.bukkit.command;

import com.gufli.kingdomcraft.api.entity.PlatformSender;
import com.gufli.kingdomcraft.bukkit.KingdomCraftBukkitPlugin;
import com.gufli.kingdomcraft.bukkit.commands.EditItemCommand;
import com.gufli.kingdomcraft.bukkit.commands.EditItemOtherCommand;
import com.gufli.kingdomcraft.bukkit.commands.ReloadCommand;
import com.gufli.kingdomcraft.bukkit.commands.VersionCommand;
import com.gufli.kingdomcraft.bukkit.entity.BukkitSender;
import com.gufli.kingdomcraft.bukkit.menu.InfoCommand;
import com.gufli.kingdomcraft.bukkit.menu.MenuCommand;
import com.gufli.kingdomcraft.common.KingdomCraftImpl;
import com.gufli.kingdomcraft.common.command.CommandManagerImpl;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandHandler implements CommandExecutor, TabCompleter {

    private final KingdomCraftBukkitPlugin plugin;
    private final KingdomCraftImpl kdc;

    public CommandHandler(KingdomCraftBukkitPlugin plugin) {
        this.plugin = plugin;
        this.kdc = plugin.getKdc();

        CommandManagerImpl cm = plugin.getKdc().getCommandManager();
        cm.addCommand(new ReloadCommand(plugin));
        cm.addCommand(new VersionCommand(plugin));
        cm.addCommand(new MenuCommand(kdc));
        cm.addCommand(new InfoCommand(kdc));
        cm.addCommand(new EditItemCommand(kdc));
        cm.addCommand(new EditItemOtherCommand(kdc));
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
            String prefix = kdc.getMessages().colorify(kdc.getMessages().getPrefix());
            sender.sendMessage(prefix + kdc.getMessages().getMessage("cmdDefaultHelp"));
            return true;
        }

        kdc.getCommandManager().dispatch(wrap(sender), args);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if ( !(sender instanceof Player) ) {
            return null;
        }
        return kdc.getCommandManager().dispatchAutocomplete(kdc.getPlayer(((Player) sender).getUniqueId()), args);
    }

    private PlatformSender wrap(CommandSender sender) {
        if ( sender instanceof Player) {
            return kdc.getPlayer(((Player) sender).getUniqueId());
        }
        return new BukkitSender(sender);
    }
}
