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

package com.gufli.kingdomcraft.common.command;

import com.gufli.kingdomcraft.api.entity.PlatformSender;
import com.gufli.kingdomcraft.api.entity.PlatformPlayer;
import org.apache.commons.text.similarity.LevenshteinDistance;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CommandDispatcher {

    private final CommandManager commandManager;

    public CommandDispatcher(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    public void execute(PlatformSender sender, String[] args) {
        if ( args.length == 0 ) {
            return;
        }

        String invalidBase = null;
        String invalidArguments = "";

        for ( CommandBase cb : commandManager.commands ) {
            for ( String cmd : cb.getCommands() ) {
                if ( !(String.join(" ", args).toLowerCase() + " ").startsWith(cmd.toLowerCase() + " ") ) {
                    continue;
                }

                int cmdLength = cmd.split(Pattern.quote(" ")).length;
                String[] cmdArgs = convertArgs(Arrays.copyOfRange(args, cmdLength, args.length), cb.getExpectedArguments());

                if ( cb.getExpectedArguments() != -1 && cmdArgs.length != cb.getExpectedArguments() ) {
                    invalidBase = cmd;
                    invalidArguments = cb.getArgumentsHint() == null ? "" : cb.getArgumentsHint();
                    continue;
                }

                if ( cb.isPlayerOnly() && !(sender instanceof PlatformPlayer) ) {
                    sender.sendMessage("This command cannot be executed in the console!");
                    return;
                }

                if ( cb.getPermissions() != null && cb.getPermissions().length > 0 ) {
                    if (Arrays.stream(cb.getPermissions()).noneMatch(sender::hasPermission)) {
                        commandManager.kdc.getMessageManager().send(sender, "cmdErrorNoPermission");
                        return;
                    }
                }

                cb.execute(sender, cmdArgs);
                return;
            }
        }

        if ( invalidBase != null ) {
            commandManager.kdc.getMessageManager().send(sender, "cmdErrorInvalidUsage",
                    "/k " + invalidBase + " " + invalidArguments);
            return;
        }

        // suggest a command if the command doesn't exist
        LevenshteinDistance ld = new LevenshteinDistance(5);

        Map<CommandBase, String[]> candidates = new HashMap<>();
        for ( CommandBase cb : commandManager.getCommands() ) {
            candidates.put(cb, cb.getCommands().get(0).split(Pattern.quote(" ")));
        }

        for ( int i = 0; i < args.length; i++ ) {
            final int index = i;

            Map<CommandBase, String[]> filter = new HashMap<>();
            candidates.forEach((cb, cbArgs) -> {
                if ( cbArgs.length > index ) {
                    filter.put(cb, cbArgs);
                }
            });

            if ( filter.size() == 0 ) {
                break;
            }

            List<CommandBase> equal = filter.keySet().stream()
                    .filter(cb -> filter.get(cb)[index].equalsIgnoreCase(args[index]))
                    .collect(Collectors.toList());

            if ( !equal.isEmpty() ) {
                candidates.entrySet().removeIf(e -> !equal.contains(e.getKey()));
                continue;
            }

            CommandBase closest = filter.keySet().stream()
                    .min(Comparator.comparingInt(cb -> {
                        int res = ld.apply(filter.get(cb)[index], args[index]);
                        return res == -1 ? Integer.MAX_VALUE : res;
                    }))
                    .orElse(null);

            if ( closest == null ) {
                break;
            }

            String arg = filter.get(closest)[index];

            List<CommandBase> keep = filter.keySet().stream()
                    .filter(cb -> filter.get(cb)[index].equalsIgnoreCase(arg))
                    .collect(Collectors.toList());

            candidates.entrySet().removeIf(e -> !keep.contains(e.getKey()));
        }

        if ( candidates.isEmpty() ) {
            commandManager.kdc.getMessageManager().send(sender, "cmdErrorInvalid");
            return;
        }

        CommandBase bestCommand = candidates.keySet().stream()
                .min(Comparator.comparingInt(cb -> {
                    int baseLength = candidates.get(cb).length;
                    int argsLength = cb.getArgumentsHint() == null ? 0 : cb.getArgumentsHint().split(Pattern.quote(" ")).length;
                    return Math.abs((baseLength + argsLength) - args.length);
                })).orElse(null);

        commandManager.kdc.getMessageManager().send(sender, "cmdErrorInvalidHint",
                "/k " + bestCommand.getCommands().get(0) +
                        (bestCommand.getArgumentsHint() != null  ? " " + bestCommand.getArgumentsHint() : ""));

    }

    public List<String> autocomplete(PlatformPlayer sender, String[] args) {

        // autocomplete command
        if ( args.length <= 1 ) {
            String arg0 = args[0].toLowerCase();
            List<String> result = new ArrayList<>();
            commandManager.commands.stream()
                    .filter(cb -> cb.getPermissions() == null || cb.getPermissions().length == 0
                            || Arrays.stream(cb.getPermissions()).anyMatch(sender::hasPermission))
                    .forEach(cb -> result.addAll(cb.getCommands().stream()
                            .map(c -> c.split(Pattern.quote(" "))[0])
                            .filter(s -> s.toLowerCase().startsWith(arg0))
                            .collect(Collectors.toList()))
                    );
            return result.stream().distinct().collect(Collectors.toList());
        }

        String input = String.join(" ", args).toLowerCase();
        List<String> result = new ArrayList<>();
        outer: for ( CommandBase cb : commandManager.commands ) {
            if ( cb.getPermissions() != null && cb.getPermissions().length > 0 ) {
                if ( Arrays.stream(cb.getPermissions()).noneMatch(sender::hasPermission) ) {
                    continue;
                }
            }

            for ( String cmd : cb.getCommands() ) {
                // check full command match
                if ( (input + " ").startsWith(cmd.toLowerCase() + " ") ) {
                    String[] cmdArgs = Arrays.copyOfRange(args, cmd.split(Pattern.quote(" ")).length, args.length);
                    List<String> options = cb.autocomplete(sender, cmdArgs);
                    if ( options != null ) {
                        result.addAll(options);
                    }
                    continue outer;
                }

                // check partial command match
                if ( cmd.toLowerCase().startsWith(input) ) {
                    result.add(cmd.split(Pattern.quote(" "))[args.length - 1]);
                    continue outer;
                }
            }
        }

        return result.stream().distinct().filter(s -> s.toLowerCase().startsWith(args[args.length - 1].toLowerCase())).collect(Collectors.toList());
    }

    private String[] convertArgs(String[] args, int expectedArgs) {
        if ( expectedArgs <= 0 ) {
            return args;
        }
        if ( args.length <= expectedArgs ) {
            return args;
        }

        List<String> currentArgs = Arrays.asList(args);

        String lastArgument = String.join(" ", currentArgs.subList(expectedArgs - 1, currentArgs.size()));
        if ( (lastArgument.startsWith("\"") && lastArgument.endsWith("\"")) || (lastArgument.startsWith("'") && lastArgument.endsWith("'")) ) {
            lastArgument = lastArgument.substring(1, lastArgument.length() - 1);

            List<String> newArgs = new ArrayList<>(currentArgs.subList(0, expectedArgs - 1));
            newArgs.add(lastArgument);
            return newArgs.toArray(new String[0]);
        }

        return args;
    }

}
