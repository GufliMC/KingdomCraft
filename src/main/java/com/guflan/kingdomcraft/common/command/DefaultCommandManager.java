package com.guflan.kingdomcraft.common.command;

import com.guflan.kingdomcraft.api.KingdomCraft;
import com.guflan.kingdomcraft.api.entity.CommandSender;
import com.guflan.kingdomcraft.api.entity.Player;
import com.guflan.kingdomcraft.common.commands.InfoCommand;
import com.guflan.kingdomcraft.common.commands.JoinCommand;
import com.guflan.kingdomcraft.common.commands.ListCommand;
import com.guflan.kingdomcraft.common.commands.admin.KickCommand;
import com.guflan.kingdomcraft.common.commands.admin.SetKingdomCommand;
import com.guflan.kingdomcraft.common.commands.management.*;
import com.guflan.kingdomcraft.api.command.CommandBase;
import com.guflan.kingdomcraft.api.command.CommandManager;
import com.guflan.kingdomcraft.common.commands.member.InviteCommand;
import com.guflan.kingdomcraft.common.commands.member.LeaveCommand;
import org.apache.commons.text.similarity.LevenshteinDistance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DefaultCommandManager implements CommandManager {

    private final KingdomCraft kdc;
    private final List<CommandBase> commands = new ArrayList<>();

    public DefaultCommandManager(KingdomCraft kdc) {
        this.kdc = kdc;
        registerAll();
    }

    public void registerAll() {
        commands.clear();

        // all
        registerCommand(new ListCommand(kdc));
        registerCommand(new JoinCommand(kdc));
        registerCommand(new InfoCommand(kdc));

        // member
        registerCommand(new LeaveCommand(kdc));
        registerCommand(new InviteCommand(kdc));

        // management
        registerCommand(new CreateCommand(kdc));
        registerCommand(new DeleteCommand(kdc));

        registerCommand(new EditDisplayCommand(kdc));
        registerCommand(new EditDisplayOtherCommand(kdc));

        registerCommand(new EditPrefixCommand(kdc));
        registerCommand(new EditPrefixOtherCommand(kdc));

        registerCommand(new EditSuffixCommand(kdc));
        registerCommand(new EditSuffixOtherCommand(kdc));

        registerCommand(new RanksListCommand(kdc));
        registerCommand(new RanksCreateCommand(kdc));

        // admin
        registerCommand(new KickCommand(kdc));
        registerCommand(new SetKingdomCommand(kdc));


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

                if ( cb.isPlayerOnly() && !(sender instanceof Player) ) {
                    sender.sendMessage("This command cannot be executed in the console!");
                    return;
                }

                String[] cmdArgs = Arrays.copyOfRange(args, argsLength, args.length);
                cb.execute(sender, cmdArgs);
                return;
            }
        }

        System.out.println("not found");

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
            sender.sendMessage("Did you mean: /k " + bestCommand.getCommands().get(0));
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
