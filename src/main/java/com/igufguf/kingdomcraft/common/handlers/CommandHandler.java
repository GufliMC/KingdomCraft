package com.igufguf.kingdomcraft.common.handlers;

import com.igufguf.kingdomcraft.bukkit.KingdomCraft;
import com.igufguf.kingdomcraft.common.domain.DPlayer;
import com.igufguf.kingdomcraft.common.commands.CommandBase;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.bukkit.ChatColor.*;

public class CommandHandler implements CommandExecutor, TabCompleter {

    private final KingdomCraft kingdomCraft;
    private final List<CommandBase> commands = new ArrayList<>();

    public CommandHandler(KingdomCraft kingdomCraft) {
        this.kingdomCraft = kingdomCraft;
    }

    public void register(CommandBase base) {
        commands.add(0, base);
    }

    public void unregister(CommandBase base) {
        commands.remove(base);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if ( sender instanceof DPlayer) {
            // TODO check enabled world
        }

        if ( args.length == 0 ) {
            String prefix = RED + BOLD.toString() + "KingdomCraft" + DARK_GRAY + BOLD + " > " + GRAY;
            sender.sendMessage(prefix + "v" + kingdomCraft.getDescription().getVersion() + " | Created by iGufGuf");
            sender.sendMessage(prefix + "https://kingdomcraft.nl");
            sender.sendMessage(prefix + GREEN + "For help type /k help");
            return true;
        }

        for ( CommandBase cb : commands ) {
            if ( cb.getCommandAliasses().stream().anyMatch(ca -> ca.equalsIgnoreCase(args[0])) ) {
                cb.executeChain(sender, Arrays.copyOfRange(args, 1, args.length));
                return true;
            }
        }

        // suggest a command if the command doesn't exist
        LevenshteinDistance ld = new LevenshteinDistance(5);
        int lowestScore = Integer.MAX_VALUE;
        CommandBase lowestCommand = null;
        for ( CommandBase cb : commands ) {
            for ( String alias : cb.getCommandAliasses() ) {
                int score = ld.apply(args[0], alias);
                if (score != -1 && score < lowestScore) {
                    lowestScore = score;
                    lowestCommand = cb;

                    if (score == 1) break;
                }
            }
        }

        if ( lowestCommand != null ) {
            kingdomCraft.messageHandler.send(sender, "cmdDefaultHint",
                    "/" + label.toLowerCase() + " " + lowestCommand.getCommandAliasses().get(0)
                            + (lowestCommand.getHint() != null ? " " + lowestCommand.getHint() : ""));
            return true;
        }

        kingdomCraft.messageHandler.send(sender, "noCommand", args[0]);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if ( !(sender instanceof org.bukkit.entity.Player) ) {
            return null;
        }

        // TODO check enabled world

        org.bukkit.entity.Player player = (org.bukkit.entity.Player) sender;
        for ( CommandBase cb : commands ) {
            if ( cb.getCommandAliasses().stream().anyMatch(ca -> ca.equalsIgnoreCase(args[0])) ) {
                return cb.autocompleteChain(player, Arrays.copyOfRange(args, 1, args.length));
            }
        }

        // autocomplete commands
        if ( args.length == 1 ) {
            List<String> result = new ArrayList<>();
            for ( CommandBase cb : commands ) {
                result.addAll(cb.getCommandAliasses().stream()
                        .filter(ca -> ca.toLowerCase().startsWith(args[0].toLowerCase()))
                        .collect(Collectors.toList())
                );
            }
            return result;
        }
        return null;
    }

}
