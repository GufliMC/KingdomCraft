package com.guflan.kingdomcraft.bukkit.command;

import com.guflan.kingdomcraft.bukkit.KingdomCraft;
import com.guflan.kingdomcraft.bukkit.entity.BukkitCommandSender;
import com.guflan.kingdomcraft.bukkit.entity.BukkitPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.List;

public class BukkitCommandExecutor implements CommandExecutor, TabCompleter {

    private final KingdomCraft plugin;

    public BukkitCommandExecutor(KingdomCraft plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if ( args.length == 0 ) {
            // TODO show gui
            return true;
        }

        plugin.getBridge().getCommandManager().execute(wrap(sender), args);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return plugin.getBridge().getCommandManager().autocomplete(wrap(sender), args);
    }

    private com.guflan.kingdomcraft.api.entity.CommandSender wrap(CommandSender sender) {
        if ( sender instanceof Player) {
            return new BukkitPlayer((Player) sender);
        }
        return new BukkitCommandSender(sender);
    }
}
