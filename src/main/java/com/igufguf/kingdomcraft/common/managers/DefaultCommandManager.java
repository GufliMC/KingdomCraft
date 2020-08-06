package com.igufguf.kingdomcraft.common.managers;

import com.igufguf.kingdomcraft.api.KingdomCraftPlugin;
import com.igufguf.kingdomcraft.api.managers.CommandManager;
import com.igufguf.kingdomcraft.api.models.CommandBase;
import com.igufguf.kingdomcraft.api.models.CommandSender;
import org.apache.commons.text.similarity.LevenshteinDistance;

import java.util.*;
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

        commands.sort(Comparator.comparing(c -> c.getArguments().length));
        Collections.reverse(commands);
    }

    @Override
    public void unregisterCommand(CommandBase command) {
        commands.remove(command);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        outer: for ( CommandBase cb : commands ) {
            if ( args.length < cb.getArguments().length + 1 ) {
                continue;
            }

            for ( String cmd : cb.getCommands() ) {
                // check for the "command" argument
                if ( !cmd.equalsIgnoreCase(args[0]) ) {
                    continue;
                }

                // check full arguments match
                for ( int i = 0; i < cb.getArguments().length; i++ ) {
                    if ( !cb.getArguments()[i].equalsIgnoreCase(args[i + 1]) ) {
                        continue outer;
                    }
                }

                String[] cmdArgs = Arrays.copyOfRange(args, cb.getArguments().length + 1, args.length);
                if ( cb.preflight(sender) ) {
                    cb.execute(sender, cmdArgs);
                }
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
            commands.forEach(cb -> result.addAll(cb.getCommands()));
            return result.stream().filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase())).collect(Collectors.toList());
        }


        List<String> result = new ArrayList<>();
        outer: for ( CommandBase cb : commands ) {
            for ( String cmd : cb.getCommands() ) {
                // check for the "command" argument
                if ( !cmd.equalsIgnoreCase(args[0]) ) {
                    continue;
                }

                // check partial arguments match
                if ( args.length - 1 < cb.getArguments().length ) {
                    for ( int i = 0; i < args.length - 2; i++ ) { // - "command" argument & - autocomplete argument
                        if ( !cb.getArguments()[i].equalsIgnoreCase(args[i + 1]) ) {
                            continue outer;
                        }
                    }

                    result.add(cb.getArguments()[args.length - 2]);
                    break;
                }

                // check full arguments match
                for ( int i = 0; i < cb.getArguments().length; i++ ) {
                    if ( !cb.getArguments()[i].equalsIgnoreCase(args[i + 1]) ) {
                        continue outer;
                    }
                }

                String[] cmdArgs = Arrays.copyOfRange(args, cb.getArguments().length + 1, args.length);
                result.addAll(cb.autocomplete(sender, cmdArgs));
                break;
            }
        }

        return result.stream().distinct().filter(s -> s.toLowerCase().startsWith(args[args.length - 1].toLowerCase())).collect(Collectors.toList());
    }
}
