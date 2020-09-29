package com.guflan.kingdomcraft.bukkit.command;

import com.guflan.kingdomcraft.api.KingdomCraftPlugin;
import com.guflan.kingdomcraft.bukkit.entity.BukkitCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

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

        plugin.getCommandManager().execute(wrap(sender), args);
        return true;
    }

    @Override
    public List<String> onTabComplete(org.bukkit.command.CommandSender sender, Command command, String alias, String[] args) {
        return plugin.getCommandManager().autocomplete(wrap(sender), args);
    }

    private com.guflan.kingdomcraft.api.entity.CommandSender wrap(CommandSender sender) {
        if ( sender instanceof Player) {
            return plugin.getPlayerManager().getOnlinePlayer(((Player) sender).getUniqueId());
        }
        return new BukkitCommandSender(sender);
    }
}
