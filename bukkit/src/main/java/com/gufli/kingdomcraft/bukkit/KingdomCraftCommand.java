package com.gufli.kingdomcraft.bukkit;

import org.bukkit.command.*;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class KingdomCraftCommand<T extends CommandExecutor & TabCompleter> extends Command implements PluginIdentifiableCommand {

    private final KingdomCraftBukkitPlugin plugin;
    private final T executor;

    public KingdomCraftCommand(KingdomCraftBukkitPlugin plugin, T executor, List<String> aliases) {
        super("kingdomcraft", "Root command for kingdomcraft sub-commands.", "/kingdomcraft", aliases);
        super.setLabel("kingdomcraft");

        this.plugin = plugin;
        this.executor = executor;
    }

    @Override
    public Plugin getPlugin() {
        return plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        try {
            return executor.onCommand(sender, this, commandLabel, args);
        } catch (Throwable ex) {
            throw new CommandException("Unhandled exception executing command '" + commandLabel + "' in plugin " + plugin.getDescription().getFullName(), ex);
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        List<String> completions;

        try {
            completions = executor.onTabComplete(sender, this, alias, args);
        } catch (Throwable ex) {
            StringBuilder message = new StringBuilder();
            message.append("Unhandled exception during tab completion for command '/").append(alias).append(' ');
            for (String arg : args) {
                message.append(arg).append(' ');
            }
            message.deleteCharAt(message.length() - 1).append("' in plugin ").append(plugin.getDescription().getFullName());
            throw new CommandException(message.toString(), ex);
        }

        if ( completions == null ) {
            completions = super.tabComplete(sender, alias, args);
        }
        return completions;
    }

}
