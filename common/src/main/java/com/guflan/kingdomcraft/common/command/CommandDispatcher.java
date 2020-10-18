package com.guflan.kingdomcraft.common.command;

import com.guflan.kingdomcraft.api.command.CommandBase;
import com.guflan.kingdomcraft.api.entity.PlatformSender;
import com.guflan.kingdomcraft.api.entity.PlatformPlayer;
import org.apache.commons.text.similarity.LevenshteinDistance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CommandDispatcher {

    private final CommandManagerImpl commandManager;

    public CommandDispatcher(CommandManagerImpl commandManager) {
        this.commandManager = commandManager;
    }

    public void execute(PlatformSender sender, String[] args) {
        if ( args.length == 0 ) {
            return;
        }

        String invalidBase = null;
        String invalidArguments = ""; // TODO

        for ( CommandBase cb : commandManager.commands ) {
            for ( String cmd : cb.getCommands() ) {
                if ( !String.join(" ", args).toLowerCase().startsWith(cmd.toLowerCase()) ) {
                    continue;
                }

                int argsLength = cmd.split(Pattern.quote(" ")).length;
                if ( args.length - argsLength != cb.getExpectedArguments() ) {
                    invalidBase = cmd;
                    continue;
                }

                if ( cb.isPlayerOnly() && !(sender instanceof PlatformPlayer) ) {
                    sender.sendMessage("This command cannot be executed in the console!");
                    return;
                }

                String[] cmdArgs = Arrays.copyOfRange(args, argsLength, args.length);
                cb.execute(sender, cmdArgs);
                return;
            }
        }

        if ( invalidBase != null ) {
            commandManager.kdc.getMessageManager().send(sender, "cmdDefaultInvalidUsage",
                    invalidBase + " " + invalidArguments);
            return;
        }

        // suggest a command if the command doesn't exist
        int bestScore = Integer.MAX_VALUE;
        CommandBase bestCommand = null;

        LevenshteinDistance ld = new LevenshteinDistance(5);
        for ( CommandBase cb : commandManager.commands ) {
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
            commandManager.kdc.getMessageManager().send(sender, "cmdDefaultInvalidHint",
                    "/k " + bestCommand.getCommands().get(0));
        } else {
            commandManager.kdc.getMessageManager().send(sender, "cmdDefaultInvalid");
        }
    }

    public List<String> autocomplete(PlatformSender sender, String[] args) {

        // autocomplete command
        if ( args.length <= 1 ) {
            List<String> result = new ArrayList<>();
            commandManager.commands.forEach(cb -> result.addAll(cb.getCommands().stream().map(c -> c.split(Pattern.quote(" "))[0]).collect(Collectors.toList())));
            return result.stream().filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase())).collect(Collectors.toList());
        }


        List<String> result = new ArrayList<>();
        outer: for ( CommandBase cb : commandManager.commands ) {
            for ( String cmd : cb.getCommands() ) {
                // check full command match
                if ( String.join(" ", args).toLowerCase().startsWith(cmd.toLowerCase()) ) {
                    String[] cmdArgs = Arrays.copyOfRange(args, cmd.split(Pattern.quote(" ")).length, args.length);
                    List<String> options = cb.autocomplete(sender, cmdArgs);
                    if ( options != null ) {
                        result.addAll(options);
                    }
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
