package com.guflan.kingdomcraft.bukkit.command;

import com.guflan.kingdomcraft.api.KingdomCraftPlugin;
import com.guflan.kingdomcraft.api.domain.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.List;

public class BukkitCommandExecutor implements CommandExecutor, TabCompleter {

    private final KingdomCraftPlugin plugin;

    public BukkitCommandExecutor(KingdomCraftPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if ( args.length == 0 ) {
            // TODO show gui
            return true;
        }

        BukkitCommandSender bcs;
        if ( sender instanceof org.bukkit.entity.Player) {
            Player player = plugin.getPlayerManager().getOnlinePlayer(((org.bukkit.entity.Player) sender).getUniqueId());
            bcs = new BukkitCommandSender(sender, player);
        } else {
            bcs = new BukkitCommandSender(sender);
        }

        plugin.getCommandManager().execute(bcs, args);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        BukkitCommandSender bcs;
        if ( sender instanceof org.bukkit.entity.Player) {
            Player player = plugin.getPlayerManager().getOnlinePlayer(((org.bukkit.entity.Player) sender).getUniqueId());
            bcs = new BukkitCommandSender(sender, player);
        } else {
            bcs = new BukkitCommandSender(sender);
        }

        return plugin.getCommandManager().autocomplete(bcs, args);
    }
}
