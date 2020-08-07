package com.igufguf.kingdomcraft.common.managers;

import com.igufguf.kingdomcraft.api.KingdomCraftPlugin;
import com.igufguf.kingdomcraft.api.managers.CommandManager;
import com.igufguf.kingdomcraft.api.commands.CommandBase;
import com.igufguf.kingdomcraft.api.commands.CommandSender;
import org.apache.commons.text.similarity.LevenshteinDistance;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DefaultCommandManager implements CommandManager {

    private final KingdomCraftPlugin plugin;
    private final List<CommandBase> commands = new ArrayList<>();

    public DefaultCommandManager(KingdomCraftPlugin plugin) {
        this.plugin = plugin;
        registerAll();
    }

    public void registerAll() {
        commands.clear();

        // TODO
    }

    @Override
    public void registerCommand(CommandBase command) {
        commands.add(command);
    }

    @Override
    public void unregisterCommand(CommandBase command) {
        commands.remove(command);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if ( args.length == 0 ) {
            return;
        }


        for ( CommandBase cb : commands ) {
            for ( String cmd : cb.getCommands() ) {
                if ( !String.join(" ", args).toLowerCase().startsWith(cmd.toLowerCase()) ) {
                    continue;
                }

                int argsLength = cmd.split(Pattern.quote(" ")).length;
                if ( args.length - argsLength != cb.getExpectedArguments() ) {
                    // TODO invalid arguments
                    continue;
                }

                if ( cb.isPlayerOnly() && sender.isConsole() ) {
                    // TODO player only
                    return;
                }

                String[] cmdArgs = Arrays.copyOfRange(args, argsLength, args.length);
                cb.execute(sender, cmdArgs);
                return;
            }
        }

        // suggest a command if the command doesn't exist
        int bestScore = Integer.MAX_VALUE;
        CommandBase bestCommand = null;

        LevenshteinDistance ld = new LevenshteinDistance(5);
        for ( CommandBase cb : commands ) {
            for ( String cmd : cb.getCommands() ) {
                int score = ld.apply(args[0], cmd);

                if (score != -1 && score < bestScore) {
                    bestScore = score;
                    bestCommand = cb;
                    if (score == 1) break;
                }
            }
        }

        if ( bestCommand != null ) {
            // TODO send message
            return;
        }

    }

    @Override
    public List<String> autocomplete(CommandSender sender, String[] args) {

        // autocomplete command
        if ( args.length <= 1 ) {
            List<String> result = new ArrayList<>();
            commands.forEach(cb -> result.addAll(cb.getCommands().stream().map(c -> c.split(Pattern.quote(" "))[0]).collect(Collectors.toList())));
            return result.stream().filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase())).collect(Collectors.toList());
        }


        List<String> result = new ArrayList<>();
        outer: for ( CommandBase cb : commands ) {
            for ( String cmd : cb.getCommands() ) {
                // check full command match
                if ( String.join(" ", args).toLowerCase().startsWith(cmd.toLowerCase()) ) {
                    String[] cmdArgs = Arrays.copyOfRange(args, cmd.split(Pattern.quote(" ")).length, args.length);
                    result.addAll(cb.autocomplete(sender, cmdArgs));
                    continue outer;
                }

                // check partial command match
                if ( cmd.toLowerCase().startsWith(String.join(" ", args).toLowerCase()) ) {
                    result.add(cmd.split(Pattern.quote(" "))[args.length - 1]);
                    continue outer;
                }
            }
        }

        return result.stream().distinct().filter(s -> s.toLowerCase().startsWith(args[args.length - 1].toLowerCase())).collect(Collectors.toList());
    }
}
