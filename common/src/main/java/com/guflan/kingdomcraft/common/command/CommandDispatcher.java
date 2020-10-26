/*
 * This file is part of KingdomCraft.
 *
 * KingdomCraft is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * KingdomCraft is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with KingdomCraft. If not, see <https://www.gnu.org/licenses/>.
 */

package com.guflan.kingdomcraft.common.command;

import com.guflan.kingdomcraft.api.entity.PlatformSender;
import com.guflan.kingdomcraft.api.entity.PlatformPlayer;
import org.apache.commons.text.similarity.LevenshteinDistance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
                if ( !String.join(" ", args).toLowerCase().startsWith(cmd.toLowerCase()) ) {
                    continue;
                }

                int cmdLength = cmd.split(Pattern.quote(" ")).length;
                if ( cb.getExpectedArguments() != -1 && args.length - cmdLength != cb.getExpectedArguments() ) {
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

                String[] cmdArgs = Arrays.copyOfRange(args, cmdLength, args.length);
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
            commandManager.kdc.getMessageManager().send(sender, "cmdErrorInvalidHint",
                    "/k " + bestCommand.getCommands().get(0));
        } else {
            commandManager.kdc.getMessageManager().send(sender, "cmdErrorInvalid");
        }
    }

    public List<String> autocomplete(PlatformPlayer sender, String[] args) {

        // autocomplete command
        if ( args.length <= 1 ) {
            List<String> result = new ArrayList<>();
            commandManager.commands.stream()
                    .filter(cb -> cb.getPermissions() == null || cb.getPermissions().length == 0
                            || Arrays.stream(cb.getPermissions()).anyMatch(sender::hasPermission))
                    .forEach(cb -> result.addAll(cb.getCommands().stream()
                            .map(c -> c.split(Pattern.quote(" "))[0])
                            .collect(Collectors.toList()))
                    );
            return result.stream().filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase())).collect(Collectors.toList());
        }

        List<String> result = new ArrayList<>();
        outer: for ( CommandBase cb : commandManager.commands ) {
            if ( cb.getPermissions() != null && cb.getPermissions().length > 0 ) {
                if ( Arrays.stream(cb.getPermissions()).noneMatch(sender::hasPermission) ) {
                    continue;
                }
            }

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
