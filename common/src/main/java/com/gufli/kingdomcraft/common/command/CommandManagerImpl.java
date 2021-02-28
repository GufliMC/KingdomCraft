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

import com.gufli.kingdomcraft.api.commands.Command;
import com.gufli.kingdomcraft.api.commands.CommandManager;
import com.gufli.kingdomcraft.api.entity.PlatformPlayer;
import com.gufli.kingdomcraft.api.entity.PlatformSender;
import com.gufli.kingdomcraft.common.KingdomCraftImpl;
import com.gufli.kingdomcraft.common.commands.admin.*;
import com.gufli.kingdomcraft.common.commands.chat.ChatChannelCommand;
import com.gufli.kingdomcraft.common.commands.chat.ChatCommand;
import com.gufli.kingdomcraft.common.commands.chat.DefaultChatChannelCommand;
import com.gufli.kingdomcraft.common.commands.edit.groups.*;
import com.gufli.kingdomcraft.common.commands.edit.kingdom.*;
import com.gufli.kingdomcraft.common.commands.edit.ranks.*;
import com.gufli.kingdomcraft.common.commands.general.HelpCommand;
import com.gufli.kingdomcraft.common.commands.general.JoinCommand;
import com.gufli.kingdomcraft.common.commands.general.ListCommand;
import com.gufli.kingdomcraft.common.commands.member.InviteCommand;
import com.gufli.kingdomcraft.common.commands.member.KickCommand;
import com.gufli.kingdomcraft.common.commands.member.LeaveCommand;
import com.gufli.kingdomcraft.common.commands.member.SetRankCommand;
import com.gufli.kingdomcraft.common.commands.relations.*;
import com.gufli.kingdomcraft.common.commands.spawn.*;
import com.gufli.kingdomcraft.common.commands.tphere.TpHereCommand;
import com.gufli.kingdomcraft.common.commands.tphere.TpHereOtherCommand;
import org.apache.commons.text.similarity.LevenshteinDistance;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CommandManagerImpl implements CommandManager {

    private final KingdomCraftImpl kdc;
    private final List<Command> commands = new ArrayList<>();

    public CommandManagerImpl(KingdomCraftImpl kdc) {
        this.kdc = kdc;
        registerDefaults();
    }

    public void addCommand(Command command) {
        if ( commands.contains(command) ) {
            return;
        }
        if ( command.getCommands().isEmpty() ) {
            return;
        }
        commands.add(command);
    }

    public void removeCommand(Command command) {
        commands.remove(command);
    }

    public List<Command> getCommands() {
        return new ArrayList<>(commands);
    }

    private void registerDefaults() {
        commands.clear();

        // all
        addCommand(new HelpCommand(kdc));
        addCommand(new ListCommand(kdc));

        addCommand(new JoinCommand(kdc));
        addCommand(new ChatChannelCommand(kdc));
        addCommand(new DefaultChatChannelCommand(kdc));

        // member
        addCommand(new LeaveCommand(kdc));
        addCommand(new SpawnCommand(kdc));
        addCommand(new InviteCommand(kdc));

        addCommand(new SetSpawnCommand(kdc));

        addCommand(new AllyCommand(kdc));
        addCommand(new AllyOtherCommand(kdc));

        addCommand(new EnemyCommand(kdc));
        addCommand(new EnemyOtherCommand(kdc));

        addCommand(new NeutralCommand(kdc));
        addCommand(new NeutralOtherCommand(kdc));

        addCommand(new TruceCommand(kdc));
        addCommand(new TruceOtherCommand(kdc));

        // admin
        addCommand(new SetRankCommand(kdc));
        addCommand(new SetKingdomCommand(kdc));
        addCommand(new KickCommand(kdc));

        addCommand(new SpawnOtherCommand(kdc));
        addCommand(new SetSpawnOtherCommand(kdc));

        addCommand(new TpSpawnCommand(kdc));
        addCommand(new TpSpawnOtherCommand(kdc));

        addCommand(new TpHereCommand(kdc));
        addCommand(new TpHereOtherCommand(kdc));

        addCommand(new ChatCommand(kdc));

        addCommand(new AdminCommand(kdc));
        addCommand(new AdminOtherCommand(kdc));
        addCommand(new SocialSpyCommand(kdc));

        addCommand(new EditorCommand(kdc));
        addCommand(new EditorSaveCommand(kdc));

        //addCommand(new PlayerDataPurgeCommand(kdc));
        addCommand(new PlayerDataClearCommand(kdc));

        /* Management */

        // kingdoms
        addCommand(new CreateCommand(kdc));

        addCommand(new DeleteCommand(kdc));
        addCommand(new DeleteOtherCommand(kdc));

        addCommand(new RenameCommand(kdc));
        addCommand(new RenameOtherCommand(kdc));

        addCommand(new EditDefaultRankCommand(kdc));
        addCommand(new EditDefaultRankOtherCommand(kdc));

        addCommand(new EditDisplayCommand(kdc));
        addCommand(new EditDisplayOtherCommand(kdc));

        addCommand(new EditPrefixCommand(kdc));
        addCommand(new EditPrefixOtherCommand(kdc));

        addCommand(new EditSuffixCommand(kdc));
        addCommand(new EditSuffixOtherCommand(kdc));

        addCommand(new EditInviteOnlyCommand(kdc));
        addCommand(new EditInviteOnlyOtherCommand(kdc));

        addCommand(new EditMaxMembersCommand(kdc));
        addCommand(new EditMaxMembersOtherCommand(kdc));

        // ranks
        addCommand(new RanksListCommand(kdc));
        addCommand(new RanksListOtherCommand(kdc));

        addCommand(new RanksCreateCommand(kdc));
        addCommand(new RanksCreateOtherCommand(kdc));

        addCommand(new RanksRenameCommand(kdc));
        addCommand(new RanksRenameOtherCommand(kdc));

        addCommand(new RanksDeleteCommand(kdc));
        addCommand(new RanksDeleteOtherCommand(kdc));

        addCommand(new RanksEditDisplayCommand(kdc));
        addCommand(new RanksEditDisplayOtherCommand(kdc));

        addCommand(new RanksEditPrefixCommand(kdc));
        addCommand(new RanksEditPrefixOtherCommand(kdc));

        addCommand(new RanksEditSuffixCommand(kdc));
        addCommand(new RanksEditSuffixOtherCommand(kdc));

        addCommand(new RanksEditMaxMembersCommand(kdc));
        addCommand(new RanksEditMaxMembersOtherCommand(kdc));

        addCommand(new RanksEditLevelCommand(kdc));
        addCommand(new RanksEditLevelOtherCommand(kdc));

        addCommand(new RanksCloneCommand(kdc));

        // groups
        addCommand(new GroupsCommand(kdc));

        addCommand(new GroupsListCommand(kdc));
        addCommand(new GroupsListOtherCommand(kdc));

        addCommand(new GroupsAddCommand(kdc));
        addCommand(new GroupsAddOtherCommand(kdc));

        addCommand(new GroupsRemoveCommand(kdc));
        addCommand(new GroupsRemoveOtherCommand(kdc));
    }

    public void dispatch(PlatformSender sender, String[] args) {
        if ( args.length == 0 ) {
            return;
        }

        String invalidBase = null;
        String invalidArguments = "";

        for ( Command cb : commands ) {
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
                        kdc.getMessages().send(sender, "cmdErrorNoPermission");
                        return;
                    }
                }

                cb.execute(sender, cmdArgs);
                return;
            }
        }

        if ( invalidBase != null ) {
            kdc.getMessages().send(sender, "cmdErrorInvalidUsage",
                    "/k " + invalidBase + " " + invalidArguments);
            return;
        }

        // suggest a command if the command doesn't exist
        LevenshteinDistance ld = new LevenshteinDistance(5);

        Map<Command, String[]> candidates = new HashMap<>();
        for ( Command cb : commands ) {
            candidates.put(cb, cb.getCommands().get(0).split(Pattern.quote(" ")));
        }

        for ( int i = 0; i < args.length; i++ ) {
            final int index = i;

            Map<Command, String[]> filter = new HashMap<>();
            candidates.forEach((cb, cbArgs) -> {
                if ( cbArgs.length > index ) {
                    filter.put(cb, cbArgs);
                }
            });

            if ( filter.size() == 0 ) {
                break;
            }

            List<Command> equal = filter.keySet().stream()
                    .filter(cb -> filter.get(cb)[index].equalsIgnoreCase(args[index]))
                    .collect(Collectors.toList());

            if ( !equal.isEmpty() ) {
                candidates.entrySet().removeIf(e -> !equal.contains(e.getKey()));
                continue;
            }

            Command closest = filter.keySet().stream()
                    .min(Comparator.comparingInt(cb -> {
                        int res = ld.apply(filter.get(cb)[index], args[index]);
                        return res == -1 ? Integer.MAX_VALUE : res;
                    }))
                    .orElse(null);

            if ( closest == null ) {
                break;
            }

            String arg = filter.get(closest)[index];

            List<Command> keep = filter.keySet().stream()
                    .filter(cb -> filter.get(cb)[index].equalsIgnoreCase(arg))
                    .collect(Collectors.toList());

            candidates.entrySet().removeIf(e -> !keep.contains(e.getKey()));
        }

        if ( candidates.isEmpty() ) {
            kdc.getMessages().send(sender, "cmdErrorInvalid");
            return;
        }

        Command bestCommand = candidates.keySet().stream()
                .min(Comparator.comparingInt(cb -> {
                    int baseLength = candidates.get(cb).length;
                    int argsLength = cb.getArgumentsHint() == null ? 0 : cb.getArgumentsHint().split(Pattern.quote(" ")).length;
                    return Math.abs((baseLength + argsLength) - args.length);
                })).orElse(null);

        kdc.getMessages().send(sender, "cmdErrorInvalidHint",
                "/k " + bestCommand.getCommands().get(0) +
                        (bestCommand.getArgumentsHint() != null  ? " " + bestCommand.getArgumentsHint() : ""));

    }

    public List<String> dispatchAutocomplete(PlatformPlayer sender, String[] args) {

        // autocomplete command
        if ( args.length <= 1 ) {
            String arg0 = args[0].toLowerCase();
            List<String> result = new ArrayList<>();
            commands.stream()
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
        outer: for ( Command cb : commands ) {
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
        if ( args.length < expectedArgs ) {
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
